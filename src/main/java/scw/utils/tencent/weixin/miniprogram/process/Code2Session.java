package scw.utils.tencent.weixin.miniprogram.process;

import scw.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.miniprogram.bean.Session;

public final class Code2Session extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/sns/jscode2session";
	private Session session;
	
	public Code2Session(String appid, String secret, String js_code){
		StringBuilder sb = new StringBuilder(API);
		sb.append("?appid=").append(appid);
		sb.append("&secret=").append(secret);
		sb.append("&js_code=").append(js_code);
		sb.append("&grant_type=authorization_code");
		JSONObject json = get(sb.toString());
		if(isSuccess()){
			this.session = new Session(json.getString("openid"), json.getString("session_key"), json.getString("unionid"));
		}
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
}
