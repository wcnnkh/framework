package shuchaowen.core.http.client.method;

import java.net.HttpURLConnection;
import java.net.Proxy;

import shuchaowen.core.http.client.HttpRequest;

public class HttpGet extends HttpRequest{
	public HttpGet(String httpUrl) {
		super(httpUrl);
		initDefault();
	}
	
	public HttpGet(String httpUrl, Proxy proxy){
		super(httpUrl, proxy);
		initDefault();
	}
	
	public HttpGet(HttpURLConnection httpURLConnection){
		super(httpURLConnection);
		initDefault();
	}
	
	private void initDefault(){
		setRequestMethod("GET");
	}
}
