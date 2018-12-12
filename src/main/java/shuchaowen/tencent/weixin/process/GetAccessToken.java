package shuchaowen.tencent.weixin.process;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.bean.AccessToken;

public final class GetAccessToken extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/token";
	private AccessToken accessToken;
		
	public GetAccessToken(String appid, String secret){
		this("client_credential", appid, secret);
	}
	
	public GetAccessToken(String grant_type, String appid, String secret){
		StringBuilder sb = new StringBuilder(API);
		sb.append("?grant_type=").append(grant_type);
		sb.append("&appid=").append(appid);
		sb.append("&secret=").append(secret);
		JSONObject json = get(sb.toString());
		if(isSuccess()){
			this.accessToken = new AccessToken(json.getString("access_token"), json.getIntValue("expires_in"));
		}
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}
}
