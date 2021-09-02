package io.basc.framework.web.servlet.http;

import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class ServletServerHttpResponse implements ServerHttpResponse, Decorator {
	private HttpServletResponse httpServletResponse;
	private HttpServletResponseHeaders headers;
	private boolean bodyUse = false;

	public ServletServerHttpResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
		this.headers = new HttpServletResponseHeaders(httpServletResponse);
	}

	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(httpServletResponse, targetType);
	}

	public OutputStream getOutputStream() throws IOException {
		bodyUse = true;
		headers.write();
		return httpServletResponse.getOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		bodyUse = true;
		headers.write();
		return httpServletResponse.getWriter();
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public boolean isCommitted() {
		return httpServletResponse.isCommitted();
	}

	public void sendError(int sc, String msg) throws IOException {
		try {
			httpServletResponse.sendError(sc, msg);
		} catch (IllegalStateException ex) {
			// Possibly on Tomcat when called too late: fall back to silent
			// setStatus
			httpServletResponse.setStatus(sc);
		}
	}

	public void addCookie(HttpCookie cookie) {
		javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
		if (cookie.getMaxAge() >= 0) {
			c.setMaxAge(cookie.getMaxAge());
		}

		if (cookie.getPath() != null) {
			c.setPath(cookie.getPath());
		}

		if (cookie.getDomain() != null) {
			c.setDomain(cookie.getDomain());
		}
		httpServletResponse.addCookie(c);
	}

	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		httpServletResponse.addCookie(cookie);
	}

	public void sendError(int sc) throws IOException {
		httpServletResponse.sendError(sc);
	}

	public void sendRedirect(String location) throws IOException {
		httpServletResponse.sendRedirect(location);
	}

	public void setStatus(int sc) {
		httpServletResponse.setStatus(sc);
	}

	public void setStatusCode(HttpStatus httpStatus) {
		httpServletResponse.setStatus(httpStatus.value());
	}

	public int getStatus() {
		return httpServletResponse.getStatus();
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public void flush() throws IOException {
		headers.write();
		if (bodyUse) {
			httpServletResponse.flushBuffer();
		}
	}

	public void close() throws IOException {
		flush();
	}
}
