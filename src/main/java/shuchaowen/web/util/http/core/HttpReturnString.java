package shuchaowen.web.util.http.core;

import java.net.HttpURLConnection;

import shuchaowen.web.util.http.impl.HttpCallBack;

public class HttpReturnString implements HttpCallBack{
	private StringBuilder sb = new StringBuilder();
	private String encode;
	public HttpReturnString(String encode) {
		this.encode = encode;
	}
	
	public void call(HttpURLConnection conn) throws Exception {
		Http.appendStr(conn.getInputStream(), sb, encode);
	}
	
	public StringBuilder getReturn(){
		return sb;
	}
}
