package scw.security.session.mvc.http.servlet;

import javax.servlet.http.HttpSession;

import scw.security.session.UserSession;

public class HttpServletUserSession<T> extends HttpServletSession implements UserSession<T> {
	private final String uidAttributeName;

	public HttpServletUserSession(HttpSession httpSession, String uidAttributeName) {
		super(httpSession);
		this.uidAttributeName = uidAttributeName;
	}

	@SuppressWarnings("unchecked")
	public T getUid() {
		return (T) getAttribute(uidAttributeName);
	}

	public void setUid(T uid) {
		setAttribute(uidAttributeName, uid);
	}
}
