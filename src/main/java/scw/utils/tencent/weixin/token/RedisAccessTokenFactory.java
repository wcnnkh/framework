package scw.utils.tencent.weixin.token;

import scw.data.redis.Redis;
import scw.utils.locks.RedisLock;
import scw.utils.tencent.weixin.AccessToken;
import scw.utils.tencent.weixin.WeiXinUtils;

public final class RedisAccessTokenFactory extends AbstractAccessTokenFactory {
	private final Redis redis;
	private final String key;
	private final String lockKey;

	public RedisAccessTokenFactory(Redis redis, String appid, String appsecret) {
		super(appid, appsecret);
		this.redis = redis;
		this.key = "wx_access_token:#" + getAppId();
		this.lockKey = "wx_access_token:#lock#" + getAppSecret();
	}

	@Override
	protected AccessToken getAccessTokenByCache() {
		return (AccessToken) redis.getObjectOperations().get(key);
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
					AccessToken accessToken = WeiXinUtils.getAccessToken(getAppId(), getAppSecret());
					if (accessToken.isSuccess()) {
						redis.getObjectOperations().setex(key, accessToken.getExpires_in(), accessToken);
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
