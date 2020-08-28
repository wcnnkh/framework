package scw.feign;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import feign.RequestTemplate;
import scw.http.HttpHeaders;
import scw.net.message.AbstractOutputMessage;
import scw.net.message.Headers;
	
public class FeignOutputMessage extends AbstractOutputMessage {
	private RequestTemplate requestTemplate;
	private OutputStream body;
	private HttpHeaders headers;

	public FeignOutputMessage(RequestTemplate requestTemplate, OutputStream body) {
		this.requestTemplate = requestTemplate;
		this.body = body;
	}

	public OutputStream getBody() throws IOException {
		return body;
	}

	public Headers getHeaders() {
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
