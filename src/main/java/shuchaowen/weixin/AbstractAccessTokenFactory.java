package shuchaowen.weixin;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.weixin.bean.AccessToken;

public abstract class AbstractAccessTokenFactory implements AccessTokenFactory{
	private final String appid;
	private final String appsecret;
	
	public AbstractAccessTokenFactory(String appid, String appsecret){
		this.appid = appid;
		this.appsecret = appsecret;
	}
	
	public String getAppid() {
		return appid;
	}

	public String getAppsecret() {
		return appsecret;
	}
	
	protected boolean isExpires(){
		AccessToken accessToken = getAccessTokenByCache();
		return accessToken == null || accessToken.isExpires();
	}
	
	public String getAccessToken() {
		AccessToken accessToken = getAccessTokenByCache();
		if(accessToken == null || accessToken.isExpires()){
			accessToken = refreshToken();
		}
		
		if(accessToken == null){
			throw new ShuChaoWenRuntimeException("无法获取token");
		}
		
		return accessToken.getAccess_token();
	}
	
	protected abstract AccessToken refreshToken();
	
	/**
	 * 从缓存中获取ticket
	 * @return
	 */
	protected abstract AccessToken getAccessTokenByCache();
}
