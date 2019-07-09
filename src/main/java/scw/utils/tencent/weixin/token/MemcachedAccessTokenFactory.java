package scw.utils.tencent.weixin.token;

import scw.data.memcached.Memcached;
import scw.utils.locks.MemcachedLock;
import scw.utils.tencent.weixin.WeiXinUtils;

public final class MemcachedAccessTokenFactory extends AbstractAccessTokenFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;

	public MemcachedAccessTokenFactory(Memcached memcached, String appid, String appsecret) {
		super(appid, appsecret);
		this.memcached = memcached;
		this.key = "wx_access_token:#" + getAppId();
		this.lockKey = "wx_access_token:#lock#" + getAppId();
	}

	@Override
	protected AccessToken getAccessTokenByCache() {
		return (AccessToken) memcached.get(key);
	}

	@Override
	protected AccessToken refreshToken() {
		if (!isExpires()) {
			return getAccessTokenByCache();
		}

		MemcachedLock memcachedLock = new MemcachedLock(memcached, lockKey);
		if (memcachedLock.lock()) {
			try {
				if (isExpires()) {
					AccessToken accessToken = WeiXinUtils.getAccessToken(getAppId(), getAppSecret());
					if (accessToken.isSuccess()) {
						memcached.set(key, accessToken.getExpires_in(), accessToken);
						return accessToken;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				memcachedLock.unlock();
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
