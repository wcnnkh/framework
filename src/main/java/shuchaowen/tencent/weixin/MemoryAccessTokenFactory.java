package shuchaowen.tencent.weixin;

import shuchaowen.tencent.weixin.bean.AccessToken;
import shuchaowen.tencent.weixin.process.GetAccessToken;

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
					GetAccessToken getAccessToken = new GetAccessToken(getAppid(), getAppsecret());
					if(getAccessToken.isSuccess()){
						accessToken = getAccessToken.getAccessToken();
					}
				}
			}
		}
		return accessToken;
	}

}
