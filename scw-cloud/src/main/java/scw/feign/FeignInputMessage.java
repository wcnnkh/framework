package scw.feign;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import feign.Response;
import scw.http.HttpHeaders;
import scw.net.message.AbstractInputMessage;
import scw.net.message.Headers;

public class FeignInputMessage extends AbstractInputMessage {
	private HttpHeaders headers;
	private Response response;

	public FeignInputMessage(Response response) {
		this.response = response;
	}

	public Headers getHeaders() {
		if (headers == null) {
			headers = new HttpHeaders();
			Map<String, Collection<String>> headerMap = response.headers();
			for (Entry<String, Collection<String>> entry : headerMap.entrySet()) {
				for (String value : entry.getValue()) {
					this.headers.add(entry.getKey(), value);
				}
			}
			this.headers.readyOnly();
		}
		return headers;
	}

	public InputStream getBody() throws IOException {
		return response.body().asInputStream();
	}
}
