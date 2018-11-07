package shuchaowen.weixin.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.StringUtils;
import shuchaowen.web.util.http.HttpPost;

public class AccessToken implements Serializable{
	private static final String weixin_get_web_token = "https://api.weixin.qq.com/cgi-bin/token";
	
	private static final long serialVersionUID = 1L;
	private String access_token;
	private int expires_in;
	private long cts;//创建时间
	
	public AccessToken(){
		this.cts = System.currentTimeMillis();
	}
	
	public AccessToken(String appid, String appsecret){
		load(appid, appsecret);
	}
	
	public void load(String appid, String appsecret){
		String url = weixin_get_web_token
				+ "?grant_type=client_credential&appid=" + appid + "&secret="
				+ appsecret;
		String content = HttpPost.invoke(url);
		if(StringUtils.isNull(content)){
			throw new ShuChaoWenRuntimeException("无法从微信服务器获取token");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(content);
		if(jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0){
			throw new ShuChaoWenRuntimeException(content);
		}
		
		this.cts = System.currentTimeMillis();
		this.access_token = jsonObject.getString("access_token");
		this.expires_in = jsonObject.getIntValue("expires_in");
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

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}
	
	//判断是否已经过期    提前5分钟过期
	public boolean isExpires(){
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
