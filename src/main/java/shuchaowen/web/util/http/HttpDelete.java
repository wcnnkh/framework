package shuchaowen.web.util.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import shuchaowen.web.util.http.core.Http;
import shuchaowen.web.util.http.impl.HttpCallBack;
import shuchaowen.web.util.http.impl.HttpOutput;
import shuchaowen.web.util.http.impl.HttpSetting;

public class HttpDelete {
	public static final String invoke(String url){
		return invoke(url, null);
	}
	
	public static final String invoke(String url, Map<String, String> paramMap){
		return invoke(url, paramMap, Http.default_encode);
	}
	
	public static final String invoke(String url, Map<String, String> paramMap, String encode){
		return invoke(url, Http.default_connectTimeout, Http.default_readTimeout, paramMap, null, Http.default_encode);
	}
	
	public static final String invoke(String url, Map<String, String> paramMap, Map<String, String> requestProperties, String encode){
		return invoke(url, Http.default_connectTimeout, Http.default_readTimeout, paramMap, requestProperties);
	}
	
	public static final String invoke(String url, int connectTimeout, int readTimeout, Map<String, String> paramMap, Map<String, String> requestProperties){
		return invoke(url, connectTimeout, readTimeout, paramMap, requestProperties, Http.default_encode);
	}
	
	public static final String invoke(String url, int connectTimeout, int readTimeout,  Map<String, String> paramMap, Map<String, String> requestProperties, String encode){
		return invoke(url, Http.getParams(paramMap).getBytes(), requestProperties, connectTimeout, readTimeout, encode);
	}
	
	public static final String invoke(String url, byte[] data, Map<String, String> requestProperties, int connectTimeout, int readTimeout){
		return invoke(url, data, requestProperties, connectTimeout, readTimeout, Http.default_encode);
	}
	
	public static final String invoke(String url, final byte[] data, Map<String, String> requestProperties, int connectTimeout, int readTimeout, final String encode){
		final StringBuilder sb = new StringBuilder();
		invoke(url, connectTimeout, readTimeout, requestProperties, new HttpOutput() {
			
			public void output(OutputStream os) throws Exception {
				if(data != null){
					os.write(data);
				}
			}
		}, new HttpCallBack() {
			
			public void call(HttpURLConnection conn) throws Exception {
				Http.appendStr(conn.getInputStream(), sb, encode);
			}
		});
		return sb.toString();
	}
	
	public static final void invoke(String url, Map<String, String> requestProperties, HttpOutput output, HttpCallBack callback){
		invoke(url, Http.default_connectTimeout, Http.default_readTimeout, requestProperties, output, callback);
	}
	
	public static final void invoke(String url, final int connectTimeout, final int readTimeout, final Map<String, String> requestProperties, HttpOutput output, HttpCallBack callback){
		Http.invoke(url, new HttpSetting() {
			
			public void setting(HttpURLConnection conn) throws Exception {
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				conn.setRequestMethod("DELETE");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				Http.setRequestPropertys(conn, requestProperties);
			}
		}, output, callback);
	}
}
