package scw.servlet.mvc.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import scw.mvc.http.HttpResponse;
import scw.net.MimeType;
import scw.net.http.Cookie;
import scw.net.http.HttpHeaders;
import scw.net.message.AbstractOutputMessage;

public class MyHttpServletResponse extends AbstractOutputMessage implements
		HttpResponse {
	private HttpServletResponse httpServletResponse;
	private HttpServletResponseHeaders headers;
	private boolean bodyUse = false;

	public MyHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
		this.headers = new HttpServletResponseHeaders(httpServletResponse);
	}

	public OutputStream getBody() throws IOException {
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

	public String getCharacterEncoding() {
		return httpServletResponse.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env) {
		httpServletResponse.setCharacterEncoding(env);
	}
	
	@Override
	public void setContentType(MimeType contentType) {
		setContentType(contentType.toString());
	}
	
	@Override
	public void setContentLength(long contentLength) {
		httpServletResponse.setContentLength((int)contentLength);
	}
	
	public void addCookie(Cookie cookie) {
		if (cookie instanceof HttpServletCookie) {
			httpServletResponse.addCookie(((HttpServletCookie) cookie)
					.getCookie());
		} else if (cookie instanceof javax.servlet.http.Cookie) {
			httpServletResponse.addCookie((javax.servlet.http.Cookie) cookie);
		} else {
			javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(
					cookie.getName(), cookie.getValue());

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
	}

	public void addCookie(String name, String value) {
		httpServletResponse
				.addCookie(new javax.servlet.http.Cookie(name, value));
	}

	public void sendError(int sc, String msg) throws IOException {
		httpServletResponse.sendError(sc, msg);
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

	public int getStatus() {
		return httpServletResponse.getStatus();
	}

	public void setContentType(String contentType) {
		httpServletResponse.setContentType(contentType);
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public void flush() throws IOException {
		headers.write();
		if(bodyUse){
			httpServletResponse.flushBuffer();
		}
	}
}
