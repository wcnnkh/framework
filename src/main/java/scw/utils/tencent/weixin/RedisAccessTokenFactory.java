package scw.utils.tencent.weixin;

import scw.data.redis.Redis;
import scw.utils.locks.RedisLock;

public final class RedisAccessTokenFactory extends AbstractAccessTokenFactory {
	private final Redis redis;
	private final String key;
	private final String lockKey;

	public RedisAccessTokenFactory(Redis redis, String appid, String appsecret) {
		super(appid, appsecret);
		this.redis = redis;
		this.key = this.getClass().getName() + "#" + getAppId();
		this.lockKey = this.getClass().getName() + "#lock#" + getAppSecret();
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
