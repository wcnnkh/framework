package shuchaowen.web.util.http;

import java.net.HttpURLConnection;
import java.util.Map;

import shuchaowen.web.util.http.core.Http;
import shuchaowen.web.util.http.impl.HttpCallBack;
import shuchaowen.web.util.http.impl.HttpSetting;

public class HttpGet {
	public final static String invoke(String url){
		return invoke(url, (Map<String, String>)null);
	}
	
	public final static String invoke(String url, Map<String, String> requestProperties){
		return invoke(url, requestProperties, Http.default_encode);
	}
	
	public final static String invoke(String url, Map<String, String> requestProperties, final String encode){
		final StringBuilder sb = new StringBuilder();
		invoke(url, requestProperties, new HttpCallBack() {
			public void call(HttpURLConnection conn) throws Exception {
				Http.appendStr(conn.getInputStream(), sb, encode);
			}
		});
		return sb.toString();
	}
	
	public final static boolean invoke(String url, HttpCallBack callback){
		return invoke(url, (Map<String, String>)null, callback);
	}
	
	public final static boolean invoke(String url, Map<String, String> requestProperties, HttpCallBack callback){
		return invoke(url, Http.default_connectTimeout, Http.default_readTimeout, requestProperties, callback);
	}
	
	public final static boolean invoke(String url, int connectTimeout, int readTimeout, HttpCallBack callback){
		return invoke(url, connectTimeout, readTimeout, null, callback);
	}
	
	public final static boolean invoke(String url, final int connectTimeout, final int readTimeout, final Map<String, String> requestProperties, HttpCallBack callback){
		return invoke(url, new HttpSetting() {
			
			public void setting(HttpURLConnection conn) throws Exception {
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				Http.setRequestPropertys(conn, requestProperties);
			}
		}, callback);
	}
	
	public final static boolean invoke(String url, HttpSetting setting, HttpCallBack callback){
		return Http.invoke(url, setting, null, callback);
	}
}
