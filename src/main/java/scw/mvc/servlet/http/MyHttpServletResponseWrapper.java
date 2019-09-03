package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import scw.net.http.Cookie;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper
		implements scw.mvc.servlet.http.HttpServletResponse {

	public MyHttpServletResponseWrapper(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	public void addCookie(Cookie cookie) {
		if (cookie instanceof ServletCookie) {
			addCookie(((ServletCookie) cookie).getCookie());
		} else {
			javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
			c.setMaxAge(cookie.getMaxAge());
			c.setPath(cookie.getPath());
			c.setDomain(cookie.getDomain());
			addCookie(c);
		}
	}

	public void setContentLength(long length) {
		setContentLengthLong(length);
	}
}
