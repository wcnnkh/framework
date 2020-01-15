package scw.net.http;

import java.io.IOException;
import java.net.Proxy;

import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpResponse;
import scw.net.mime.MimeType;

public interface HttpClient {
	ClientHttpRequest create(String url, Proxy proxy, Method method) throws IOException;

	String doGet(String url) throws IOException;

	String doGet(String url, String charsetName) throws IOException;

	ClientHttpResponse doPost(String url, byte[] body, MimeType contentType) throws IOException;
}
