package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import scw.mvc.MVCUtils;
import scw.mvc.http.HttpRequest;
import scw.mvc.servlet.ServletUtils;
import scw.net.http.Cookie;
import scw.session.Session;
import scw.session.mvc.http.servlet.HttpServletSession;

public class MyHttpServletRequest extends HttpServletRequestWrapper implements HttpServletRequest, HttpRequest {

	public MyHttpServletRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public String getRequestPath() {
		return getServletPath();
	}

	public Cookie getCookie(String name, boolean ignoreCase) {
		if (name == null) {
			return null;
		}

		javax.servlet.http.Cookie cookie = ServletUtils.getCookie(this, name, ignoreCase);
		if (cookie == null) {
			return null;
		}

		return new HttpServletCookie(cookie);
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
