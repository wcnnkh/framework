package shuchaowen.web.support.weixin.impl;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.web.support.weixin.WeiXinAccessInfo;
import shuchaowen.web.util.WeiXinUtils;

public abstract class WeiXinAccess {
	private String appId;
	private String appsecret;

	public WeiXinAccess(String appId, String appsecret) {
		this.appId = appId;
		this.appsecret = appsecret;
	}
	
	public String getAppId() {
		return appId;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public abstract WeiXinAccessInfo refershAccessToken();
	
	public abstract WeiXinAccessInfo getCacheAccessToken();
	
	public String getAccessToken(){
		WeiXinAccessInfo info = getCacheAccessToken();
		if (info == null || info.getAccess() == null ||  info.isExpires()) {
			synchronized (this) {
				if (info == null || info.getAccess() == null ||  info.isExpires()) {
					info = refershAccessToken();
				}
			}
		}
		return info.getAccess();
	}
	
	public final WeiXinAccessInfo getNewAccessToken() {
		WeiXinAccessInfo info = new WeiXinAccessInfo();
		String content = WeiXinUtils.getWebToken(getAppId(), getAppsecret());
		JSONObject json;
		int expires_in;
		try {
			json = JSONObject.parseObject(content);
			expires_in = json.getIntValue("expires_in");
			if (expires_in == 0) {
				expires_in = 7200;
			}

			info.setAccess(json.getString("access_token"));
			info.setExpires(expires_in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	public abstract WeiXinAccessInfo refreshAccessTicket();
	
	public abstract WeiXinAccessInfo getCacheAccessTicket();
	
	public String getAccessTicket(){
		WeiXinAccessInfo info = getCacheAccessTicket();
		if (info == null || info.getAccess() == null || info.isExpires()) {
			synchronized (this) {
				info = getCacheAccessTicket();
				if (info == null || info.getAccess() == null || info.isExpires()) {
					info = refreshAccessTicket();
				}
			}
		}
		return info.getAccess();
	}
	
	public final WeiXinAccessInfo getNewAccessTicket() {
		WeiXinAccessInfo info = new WeiXinAccessInfo();
		String token =  getAccessToken();
		String content =  WeiXinUtils.getWebTicket(token);
		JSONObject json;
		int expires_in;
		try {
			json = JSONObject.parseObject(content);
			if (json.getInteger("errcode") != 0) {
				throw new NullPointerException(
						"refresh token error, code not is 0");
			}

			expires_in = json.getIntValue("expires_in");
			if (expires_in == 0) {
				expires_in = 7200;
			}
			
			info.setAccess(json.getString("ticket"));
			info.setExpires(expires_in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}
