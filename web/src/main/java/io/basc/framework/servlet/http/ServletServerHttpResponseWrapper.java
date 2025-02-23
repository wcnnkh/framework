package io.basc.framework.servlet.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.servlet.AbstractServletServerResponseWrapper;

public class ServletServerHttpResponseWrapper extends AbstractServletServerResponseWrapper<HttpServletResponse>
		implements ServerHttpResponse {
	private HttpServletResponseHeaders headers;

	public ServletServerHttpResponseWrapper(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.headers = new HttpServletResponseHeaders(httpServletResponse);
	}

	public OutputStream getOutputStream() throws IOException {
		headers.write();
		return super.getOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		headers.write();
		return super.getWriter();
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void sendError(int sc, String msg) throws IOException {
		try {
			source.sendError(sc, msg);
		} catch (IllegalStateException ex) {
			// Possibly on Tomcat when called too late: fall back to silent
			// setStatus
			source.setStatus(sc);
		}
	}

	public void addCookie(HttpCookie cookie) {
		source.addCookie(ServletCookieCodec.INSTANCE.decode(cookie));
	}

	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		source.addCookie(cookie);
	}

	public void sendError(int sc) throws IOException {
		source.sendError(sc);
	}

	public void sendRedirect(String location) throws IOException {
		source.sendRedirect(location);
	}

	public void setStatus(int sc) {
		source.setStatus(sc);
	}

	public void setStatusCode(HttpStatus httpStatus) {
		source.setStatus(httpStatus.getCode());
	}

	public int getStatus() {
		return source.getStatus();
	}

	public void flush() throws IOException {
		headers.write();
		super.flush();
	}
}
