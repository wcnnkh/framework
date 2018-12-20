package scw.tencent.weixin;

import java.io.IOException;
import java.nio.charset.Charset;

import scw.common.io.IOUtils;
import scw.locks.RedisLock;
import scw.redis.Redis;
import scw.tencent.weixin.bean.AccessToken;
import scw.tencent.weixin.process.GetAccessToken;

public final class RedisAccessTokenFactory extends AbstractAccessTokenFactory {
	private final Redis redis;
	private final byte[] key;
	private final String lockKey;

	public RedisAccessTokenFactory(Redis redis, String charsetName, String appid, String appsecret) {
		this(redis, Charset.forName(charsetName), appid, appsecret);
	}

	public RedisAccessTokenFactory(Redis redis, Charset charset, String appid, String appsecret) {
		super(appid, appsecret);
		this.redis = redis;
		this.key = (this.getClass().getName() + "#" + getAppid()).getBytes(charset);
		this.lockKey = this.getClass().getName() + "#lock#" + getAppid();
	}

	@Override
	protected AccessToken getAccessTokenByCache() {
		byte[] data = redis.get(key);
		if (data == null) {
			return null;
		}

		try {
			return IOUtils.byteToJavaObject(data);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected AccessToken refreshToken() {
		if (!isExpires()) {
			return getAccessTokenByCache();
		}

		RedisLock lock = new RedisLock(redis, lockKey);
		if (lock.lock()) {
			try {
				if (isExpires()) {
					GetAccessToken getAccessToken = new GetAccessToken(getAppid(), getAppsecret());
					if (getAccessToken.isSuccess()) {
						AccessToken accessToken = getAccessToken.getAccessToken();
						redis.setex(key, accessToken.getExpires_in(), IOUtils.javaObjectToByte(accessToken));
						return accessToken;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		// 没有拿到锁
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return refreshToken();
	}
}
