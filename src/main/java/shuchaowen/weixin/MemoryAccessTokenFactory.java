package shuchaowen.weixin;

import shuchaowen.weixin.bean.AccessToken;

public final class MemoryAccessTokenFactory extends AbstractAccessTokenFactory{
	private volatile AccessToken accessToken;
	private volatile Object lock = new Object();
	
	public MemoryAccessTokenFactory(String appid, String appsecret) {
		super(appid, appsecret);
	}
	
	@Override
	protected AccessToken getAccessTokenByCache() {
		return accessToken;
	}

	@Override
	protected AccessToken refreshToken() {
		if(isExpires()){
			synchronized(lock){
				if(isExpires()){
					accessToken = new AccessToken(getAppid(), getAppsecret());
				}
			}
		}
		return accessToken;
	}

}
