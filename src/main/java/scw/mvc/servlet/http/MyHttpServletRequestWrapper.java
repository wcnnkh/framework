package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import scw.login.Session;
import scw.net.http.Cookie;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper
		implements scw.mvc.servlet.http.HttpServletRequest {

	public MyHttpServletRequestWrapper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public String getRequestPath() {
		return getServletPath();
	}

	public Cookie getCookie(String name, boolean ignoreCase) {
		if (name == null) {
			return null;
		}

		javax.servlet.http.Cookie[] cookies = getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}

		for (javax.servlet.http.Cookie cookie : cookies) {
			if (cookie == null) {
				continue;
			}

			if (ignoreCase) {
				if (name.equalsIgnoreCase(cookie.getName())) {
					return new ServletCookie(cookie);
				}
			} else {
				if (name.equals(cookie.getName())) {
					return new ServletCookie(cookie);
				}
			}
		}
		return null;
	}

	public Session getHttpSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getHttpSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

}
