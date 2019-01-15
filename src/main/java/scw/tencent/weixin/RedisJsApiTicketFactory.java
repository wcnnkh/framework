package scw.tencent.weixin;

import java.nio.charset.Charset;

import scw.common.io.IOUtils;
import scw.locks.RedisLock;
import scw.redis.Redis;
import scw.tencent.weixin.bean.JsApiTicket;
import scw.tencent.weixin.process.GetJsApiTicket;

public final class RedisJsApiTicketFactory extends AbstractJsApiTicketFactory {
	private final Redis redis;
	private final byte[] key;
	private final String lockKey;

	public RedisJsApiTicketFactory(Redis redis, String appid, String appsecret, String charsetName) {
		this(redis, charsetName, appid, new RedisAccessTokenFactory(redis, charsetName, appid, appsecret));
	}

	public RedisJsApiTicketFactory(Redis redis, String appid, String appsecret, Charset charset) {
		this(redis, charset, appid, new RedisAccessTokenFactory(redis, charset, appid, appsecret));
	}

	public RedisJsApiTicketFactory(Redis redis, String charsetName, String key, AccessTokenFactory accessTokenFactory) {
		this(redis, Charset.forName(charsetName), key, accessTokenFactory);
	}

	public RedisJsApiTicketFactory(Redis redis, Charset charset, String key, AccessTokenFactory accessTokenFactory) {
		super(accessTokenFactory);
		this.redis = redis;
		this.key = (this.getClass().getName() + "#" + key).getBytes(charset);
		this.lockKey = this.getClass().getName() + "#lock#" + key;
	}

	@Override
	protected JsApiTicket getJsApiTicketByCache() {
		byte[] data = redis.get(key);
		if (data == null) {
			return null;
		}

		return IOUtils.byteToJavaObject(data);
	}

	@Override
	protected JsApiTicket refreshJsApiTicket() {
		if (!isExpires()) {
			return getJsApiTicketByCache();
		}

		RedisLock lock = new RedisLock(redis, lockKey);
		if (lock.lock()) {
			try {
				if (isExpires()) {
					GetJsApiTicket getJsApiTicket = new GetJsApiTicket(getAccessToken());
					if (getJsApiTicket.isSuccess()) {
						JsApiTicket jsApiTicket = getJsApiTicket.getTicket();
						redis.setex(key, jsApiTicket.getExpires_in(), IOUtils.javaObjectToByte(jsApiTicket));
						return jsApiTicket;
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
		return refreshJsApiTicket();
	}
}
