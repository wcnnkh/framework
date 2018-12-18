package scw.tencent.weixin.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.common.utils.SignUtils;
import scw.common.utils.StringUtils;

public final class JsApiSignature implements Serializable{
	private static final long serialVersionUID = 1L;
	private String nonceStr;//注意 这个随机字符串的S在前端是大写的，可是在签名的时候是小写的
	private int timestamp;//单位：秒
	private String url;
	private String signature;
	
	/**
	 * 这个构造方法是用于序列化的，请不要随意使用，除非你已经知道是什么意思
	 */
	public JsApiSignature(){}
	
	public JsApiSignature(String jsapi_ticket, String url){
		this(StringUtils.getRandomStr(10), jsapi_ticket, (int)(System.currentTimeMillis()/1000), url);
	}
	
	public JsApiSignature(String nonceStr, String jsapi_ticket, int timestamp, String url){
		this.nonceStr = nonceStr;
		this.timestamp = timestamp;
		this.url = url;
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("noncestr", nonceStr);
		map.put("timestamp", timestamp + "");
		map.put("url", url);
		map.put("jsapi_ticket", jsapi_ticket);

		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			sb.append(key).append("=").append(map.get(key));
			if (i < keys.length - 1) {
				sb.append("&");
			}
		}
		this.signature = SignUtils.sha1(sb.toString(), "UTF-8");
	}
	
	public String getNonceStr() {
		return nonceStr;
	}
	
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
