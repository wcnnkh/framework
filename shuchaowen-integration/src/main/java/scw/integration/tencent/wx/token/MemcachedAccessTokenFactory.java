package scw.integration.tencent.wx.token;

import scw.core.annotation.ParameterName;
import scw.data.memcached.Memcached;
import scw.integration.tencent.wx.AccessToken;
import scw.integration.tencent.wx.WeiXinUtils;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.locks.MemcachedLockFactory;

public final class MemcachedAccessTokenFactory extends AbstractAccessTokenFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;
	private final LockFactory lockFactory;

	public MemcachedAccessTokenFactory(Memcached memcached, @ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret) {
		super(appid, appsecret);
		this.memcached = memcached;
		this.key = "wx_access_token:#" + getAppId();
		this.lockKey = "wx_access_token:#lock#" + getAppId();
		this.lockFactory = new MemcachedLockFactory(memcached);
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

		Lock memcachedLock = lockFactory.getLock(lockKey);
		if (memcachedLock.tryLock()) {
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
