package scw.data.file;

import scw.core.Converter;
import scw.net.HttpMessage;
import scw.net.http.HttpRequest;
import scw.net.http.Method;

public class HttpMessageCacheConvert implements Converter<String, HttpMessage> {
	private boolean trustAllSSL;
	
	public HttpMessageCacheConvert(boolean trustAllSSL){
		this.trustAllSSL = trustAllSSL;
	}
	
	public boolean isTrustAllSSL() {
		return trustAllSSL;
	}

	public void setTrustAllSSL(boolean trustAllSSL) {
		this.trustAllSSL = trustAllSSL;
	}

	public HttpMessage convert(String url) throws Exception {
		HttpRequest httpRequest = new HttpRequest(Method.GET, url, isTrustAllSSL());
		return httpRequest.execute();
	}
}
