package scw.security.session.mvc.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.core.exception.NotSupportException;
import scw.mvc.http.HttpRequest;
import scw.security.session.Session;
import scw.security.session.mvc.http.HttpSessionFactory;

public final class HttpServletSessionFactory implements HttpSessionFactory {

	public Session getSession(HttpRequest httpRequest) {
		return getSession(httpRequest, false);
	}

	public Session getSession(HttpRequest httpRequest, boolean create) {
		if (!(httpRequest instanceof HttpServletRequest)) {
			throw new NotSupportException("这不是一个HttpServletRequest请求:" + httpRequest.getClass());
		}

		HttpSession httpSession = ((HttpServletRequest) httpRequest).getSession(create);
		if (httpSession == null) {
			return null;
		}
		
		return new HttpServletSession(httpSession);
	}
}
