package scw.net.http;

import java.io.IOException;

import scw.io.IOUtils;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public abstract class AbstractHttpClient implements HttpClient {

	public String doGet(String url) throws IOException {
		ClientHttpRequest httpRequest = create(url, null, Method.GET);
		httpRequest.setContentType(MimeTypeUtils.APPLICATION_FORM_URLENCODED);
		ClientHttpResponse httpResponse = httpRequest.execute();
		return httpResponse.toString();
	}

	public String doGet(String url, String charsetName) throws IOException {
		ClientHttpRequest httpRequest = create(url, null, Method.GET);
		httpRequest.setContentType(new MimeType(
				MimeTypeUtils.APPLICATION_FORM_URLENCODED, charsetName));
		ClientHttpResponse httpResponse = httpRequest.execute();
		return httpResponse.toString();
	}

	public ClientHttpResponse doPost(String url, byte[] body,
			MimeType contentType) throws IOException {
		ClientHttpRequest httpRequest = create(url, null, Method.POST);
		httpRequest.setContentType(contentType);
		IOUtils.write(body, httpRequest.getBody());
		return httpRequest.execute();
	}

}
