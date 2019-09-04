package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import scw.login.Session;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpRequest;
import scw.net.http.Cookie;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper
		implements HttpServletRequest, HttpRequest {

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
					return new HttpServletCookie(cookie);
				}
			} else {
				if (name.equals(cookie.getName())) {
					return new HttpServletCookie(cookie);
				}
			}
		}
		return null;
	}

	public Session getHttpSession() {
		HttpSession session = getSession();
		return session == null ? null : new HttpServletSession(session);
	}

	public Session getHttpSession(boolean create) {
		HttpSession httpSession = getSession(create);
		return new HttpServletSession(httpSession);
	}

	public String getIP() {
		return MVCUtils.getIP(this);
	}

	public boolean isAjax() {
		return MVCUtils.isAjaxRequest(this);
	}

}
