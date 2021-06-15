package scw.feign;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import scw.http.HttpHeaders;
import scw.http.HttpOutputMessage;
import feign.RequestTemplate;

public class FeignOutputMessage implements HttpOutputMessage {
	private RequestTemplate requestTemplate;
	private OutputStream body;
	private HttpHeaders headers;

	public FeignOutputMessage(RequestTemplate requestTemplate, OutputStream body) {
		this.requestTemplate = requestTemplate;
		this.body = body;
	}

	public OutputStream getOutputStream() throws IOException {
		return body;
	}

	public HttpHeaders getHeaders() {
		if (headers == null) {
			headers = new HttpHeaders();
			Map<String, Collection<String>> headerMap = requestTemplate.headers();
			for (Entry<String, Collection<String>> entry : headerMap.entrySet()) {
				for (String value : entry.getValue()) {
					this.headers.add(entry.getKey(), value);
				}
			}
			this.headers.readyOnly();
		}
		return headers;
	}

}
