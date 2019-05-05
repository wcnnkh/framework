package scw.utils.tencent.weixin;

import scw.data.memcached.Memcached;
import scw.locks.MemcachedLock;
import scw.utils.tencent.weixin.bean.AccessToken;
import scw.utils.tencent.weixin.process.GetAccessToken;

public final class MemcachedAccessTokenFactory extends AbstractAccessTokenFactory{
	private final Memcached memcached;
	private final String key;
	private final String lockKey;
	
	public MemcachedAccessTokenFactory(Memcached memcached, String appid, String appsecret) {
		super(appid, appsecret);
		this.memcached = memcached;
		this.key = this.getClass().getName() + "#" + getAppid();
		this.lockKey = this.getClass().getName() + "#lock#" + getAppid();
	}
	
	@Override
	protected AccessToken getAccessTokenByCache() {
		return (AccessToken) memcached.get(key);
	}

	@Override
	protected AccessToken refreshToken() {
		if(!isExpires()){
			return getAccessTokenByCache();
		}
		
		MemcachedLock memcachedLock = new MemcachedLock(memcached, lockKey);
		if(memcachedLock.lock()){
			try {
				if(isExpires()){
					GetAccessToken getAccessToken = new GetAccessToken(getAppid(), getAppsecret());
					if(getAccessToken.isSuccess()){
						AccessToken accessToken = getAccessToken.getAccessToken();
						memcached.set(key, accessToken.getExpires_in(), accessToken);
						return accessToken;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				memcachedLock.unlock();
			}
		}
		
		//没有拿到锁
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return refreshToken();
	}
}
