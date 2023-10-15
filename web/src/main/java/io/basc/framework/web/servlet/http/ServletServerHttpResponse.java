package io.basc.framework.web.servlet.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.servlet.ServletServerResponse;

public class ServletServerHttpResponse extends ServletServerResponse<HttpServletResponse>
		implements ServerHttpResponse {
	private HttpServletResponseHeaders headers;

	public ServletServerHttpResponse(HttpServletResponse httpServletResponse) {
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
			wrappedTarget.sendError(sc, msg);
		} catch (IllegalStateException ex) {
			// Possibly on Tomcat when called too late: fall back to silent
			// setStatus
			wrappedTarget.setStatus(sc);
		}
	}

	public void addCookie(HttpCookie cookie) {
		wrappedTarget.addCookie(ServletCookieCodec.INSTANCE.decode(cookie));
	}

	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		wrappedTarget.addCookie(cookie);
	}

	public void sendError(int sc) throws IOException {
		wrappedTarget.sendError(sc);
	}

	public void sendRedirect(String location) throws IOException {
		wrappedTarget.sendRedirect(location);
	}

	public void setStatus(int sc) {
		wrappedTarget.setStatus(sc);
	}

	public void setStatusCode(HttpStatus httpStatus) {
		wrappedTarget.setStatus(httpStatus.value());
	}

	public int getStatus() {
		return wrappedTarget.getStatus();
	}

	public void flush() throws IOException {
		headers.write();
		super.flush();
	}
}
