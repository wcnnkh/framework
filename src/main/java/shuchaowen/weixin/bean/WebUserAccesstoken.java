package shuchaowen.weixin.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.StringUtils;
import shuchaowen.web.util.http.HttpPost;

/**
 * 用code换取的信息
 * @author shuchaowen
 *
 */
public class WebUserAccesstoken implements Serializable{
	private static final String weixin_get_web_access_token = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private static final String weixin_get_web_refresh_access_token = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	
	private static final long serialVersionUID = 1L;
	private String access_token;
	private int expires_in;
	private String refresh_token;
	private String openid;
	private String scope;
	
	/**
	 * 用于序列化
	 */
	public WebUserAccesstoken(){};
	
	/**
	 * 第一次获取
	 * @param appId
	 * @param appsecret
	 * @param code
	 */
	public WebUserAccesstoken(String appid, String appsecret, String code){
		load(appid, appsecret, code);
	}
	
	/**
	 * 刷新
	 * @param appid
	 * @param refresh_token
	 */
	public WebUserAccesstoken(String appid, String refresh_token){
		load(appid, refresh_token);
	}
	
	public void load(String appid, String appsecret, String code){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appid);
		map.put("secret", appsecret);
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		String jsonData = HttpPost.invoke(weixin_get_web_access_token, map);
		
		if(StringUtils.isNull(jsonData)){
			throw new ShuChaoWenRuntimeException("无法获取web_user_access_token");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(jsonData);
		if(jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0){
			throw new ShuChaoWenRuntimeException(jsonData);
		}
		
		this.access_token = jsonObject.getString("access_token");
		this.expires_in = jsonObject.getIntValue("expires_in");
		this.refresh_token = jsonObject.getString("refresh_token");
		this.openid = jsonObject.getString("openid");
		this.scope = jsonObject.getString("scope");
	}
	
	public void load(String appid, String refresh_token){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appid);
		map.put("grant_type", "refresh_token");
		map.put("refresh_token", refresh_token);
		String content = HttpPost.invoke(weixin_get_web_refresh_access_token, map);
		if(StringUtils.isNull(content)){
			throw new ShuChaoWenRuntimeException("无法获取web_user_access_token");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(content);
		if(jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0){
			throw new ShuChaoWenRuntimeException(content);
		}
		
		this.access_token = jsonObject.getString("access_token");
		this.expires_in = jsonObject.getIntValue("expires_in");
		this.refresh_token = jsonObject.getString("refresh_token");
		this.openid = jsonObject.getString("openid");
		this.scope = jsonObject.getString("scope");
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
