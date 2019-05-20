package scw.servlet.http.session;

import javax.servlet.http.HttpServletResponse;

import scw.servlet.http.HttpRequest;
import scw.utils.login.LoginFactory;
import scw.utils.login.Session;

/**
 * 此类和AppSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public class WebSession extends AppSession {
	public WebSession(HttpRequest request, LoginFactory loginFactory, String uidKey, String sidKey) {
		super(request, loginFactory, uidKey, sidKey, true);
	}

	public Session login(HttpServletResponse httpServletResponse, String uid) {
		Session session = super.login(uid);
		addCookie(httpServletResponse);
		return session;
	}
}
