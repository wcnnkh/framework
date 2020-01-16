package scw.testing.http;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import scw.io.ByteArray;
import scw.io.IOUtils;
import scw.mvc.http.HttpRequest;

public final class SimpleHttpTestingRequestMessage implements Serializable, HttpTestingRequestMessage {
	private static final long serialVersionUID = 1L;
	private Map<String, String> header;
	private ByteArray body;
	private String method;
	private String path;

	protected SimpleHttpTestingRequestMessage() {

	}

	public SimpleHttpTestingRequestMessage(HttpRequest request) throws IOException {
		Enumeration<String> enumeration = request.getHeaderNames();
		this.header = new HashMap<String, String>();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			header.put(name, request.getHeader(name));
		}

		this.method = request.getRawMethod();
		this.path = request.getControllerPath();
		this.body = IOUtils.read(request.getBody(), 1024, 0);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public ByteArray getBody() {
		return body;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}
}
