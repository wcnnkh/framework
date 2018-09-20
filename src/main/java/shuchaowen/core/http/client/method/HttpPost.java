package shuchaowen.core.http.client.method;

import java.net.HttpURLConnection;
import java.net.Proxy;

import shuchaowen.core.http.client.HttpRequest;
import shuchaowen.core.http.client.parameter.MultipartFormParameter;

public class HttpPost extends HttpRequest{
	public HttpPost(String httpUrl) {
		super(httpUrl);
		initDefault();
	}
	
	public HttpPost(String httpUrl, Proxy proxy){
		super(httpUrl, proxy);
		initDefault();
	}
	
	public HttpPost(HttpURLConnection httpURLConnection){
		super(httpURLConnection);
		initDefault();
	}
	
	private void initDefault(){
		setRequestMethod("POST");
	}

	public void setMultipartFormDataBoundary(){
		setRequestProperties("Content-Type", "multipart/form-data; boundary=" + MultipartFormParameter.BOUNDARY);
	}
}
