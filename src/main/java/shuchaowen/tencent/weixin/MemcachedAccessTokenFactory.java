package shuchaowen.tencent.weixin;

import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedLock;
import shuchaowen.tencent.weixin.bean.AccessToken;

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
		return memcached.get(key);
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
					AccessToken accessToken = new AccessToken(getAppid(), getAppsecret());
					if(accessToken != null){
						memcached.set(key, accessToken.getExpires_in(), accessToken);
						return accessToken;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				memcachedLock.unLock();
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
