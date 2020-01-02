package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import scw.mvc.http.HttpResponse;
import scw.net.http.Cookie;

public class MyHttpServletResponse extends HttpServletResponseWrapper
		implements HttpServletResponse, HttpResponse {

	public MyHttpServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	public void addCookie(Cookie cookie) {
		if (cookie instanceof HttpServletCookie) {
			addCookie(((HttpServletCookie) cookie).getCookie());
		} else {
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
			addCookie(c);
		}
	}

	public void setContentLength(long length) {
		setContentLengthLong(length);
	}

	public void addCookie(String name, String value) {
		addCookie(new javax.servlet.http.Cookie(name, value));
	}
}
