package scw.session.mvc.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.core.exception.NotSupportException;
import scw.mvc.http.HttpRequest;
import scw.session.UserSession;
import scw.session.mvc.http.HttpUserSessionFactory;

public final class HttpServletUserSessionFactory<T> implements HttpUserSessionFactory<T> {
	private final String uidAttributeName;

	public HttpServletUserSessionFactory() {
		this("_uid");
	}

	public HttpServletUserSessionFactory(String uidAttributeName) {
		this.uidAttributeName = uidAttributeName;
	}

	public UserSession<T> getUserSession(HttpRequest httpRequest) {
		return getUserSession(httpRequest, false);
	}

	public UserSession<T> getUserSession(HttpRequest httpRequest, boolean create) {
		if (!(httpRequest instanceof HttpServletRequest)) {
			throw new NotSupportException("这不是一个HttpServletRequest请求:" + httpRequest.getClass());
		}

		HttpSession httpSession = ((HttpServletRequest) httpRequest).getSession(create);
		if (httpSession == null) {
			return null;
		}

		return new HttpServletUserSession<T>(httpSession, uidAttributeName);
	}
}
