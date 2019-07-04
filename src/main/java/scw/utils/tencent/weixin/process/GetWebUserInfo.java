package scw.utils.tencent.weixin.process;

import java.util.HashMap;
import java.util.Map;

import scw.core.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.bean.WebUserInfo;

public final class GetWebUserInfo extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/sns/userinfo";
	private WebUserInfo webUserInfo;
	
	public GetWebUserInfo(String openid, String user_access_token){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", user_access_token);
		paramMap.put("openid", openid);
		paramMap.put("lang", "zh_CN");
		JSONObject json = post(API, paramMap);
		if(isSuccess()){
			this.webUserInfo = new WebUserInfo(json.getString("openid"), 
					json.getString("nickname"), json.getIntValue("sex"), 
					json.getString("province"), json.getString("city"), json.getString("country"), 
					json.getString("headimgurl"), json.getString("privilege"), json.getString("unionid"));
		}
	}

	public WebUserInfo getWebUserInfo() {
		return webUserInfo;
	}
}
