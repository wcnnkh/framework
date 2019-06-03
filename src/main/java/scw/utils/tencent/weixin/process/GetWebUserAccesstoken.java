package scw.utils.tencent.weixin.process;

import java.util.HashMap;
import java.util.Map;

import scw.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.bean.WebUserAccesstoken;

public final class GetWebUserAccesstoken extends WeiXinProcess{
	private static final String weixin_get_web_access_token = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private static final String weixin_get_web_refresh_access_token = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	
	private WebUserAccesstoken webUserAccesstoken;
	
	public GetWebUserAccesstoken(String appid, String appsecret, String code){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appid);
		map.put("secret", appsecret);
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		JSONObject json = post(weixin_get_web_access_token, map);
		if(isSuccess()){
			this.webUserAccesstoken = new WebUserAccesstoken(json.getString("access_token"), json.getIntValue("expires_in"), json.getString("refresh_token"), json.getString("openid"), json.getString("scope"));
		}
	}
	
	public GetWebUserAccesstoken(String appid, String refresh_token){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appid);
		map.put("grant_type", "refresh_token");
		map.put("refresh_token", refresh_token);
		JSONObject json = post(weixin_get_web_refresh_access_token, map);
		if(isSuccess()){
			this.webUserAccesstoken = new WebUserAccesstoken(json.getString("access_token"), json.getIntValue("expires_in"), json.getString("refresh_token"), json.getString("openid"), json.getString("scope"));
		}
	}

	public WebUserAccesstoken getWebUserAccesstoken() {
		return webUserAccesstoken;
	}
}
