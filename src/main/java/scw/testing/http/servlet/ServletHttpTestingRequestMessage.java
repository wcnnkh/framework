package scw.testing.http.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.core.io.ByteArray;
import scw.core.utils.IOUtils;
import scw.testing.http.HttpTestingRequestMessage;

public final class ServletHttpTestingRequestMessage implements Serializable, HttpTestingRequestMessage {
	private static final long serialVersionUID = 1L;
	private Map<String, String> header;
	private ByteArray body;
	private String method;
	private String path;

	protected ServletHttpTestingRequestMessage() {

	}

	public ServletHttpTestingRequestMessage(HttpServletRequest request) throws IOException {
		Enumeration<String> enumeration = request.getHeaderNames();
		this.header = new HashMap<String, String>();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			header.put(name, request.getHeader(name));
		}

		this.method = request.getMethod();
		this.path = request.getServletPath();
		this.body = IOUtils.read(request.getInputStream(), 1024, 0);
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
