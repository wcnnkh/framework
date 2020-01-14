package scw.net.http;

import java.net.Proxy;

import scw.net.mime.MimeType;

public interface HttpClient {
	ClientHttpRequest create(String url, Proxy proxy, Method method);

	String doGet(String url);

	String doGet(String url, String charsetName);

	String doPost(String url, String charsetName, byte[] body, MimeType contentType);
	
	
}
