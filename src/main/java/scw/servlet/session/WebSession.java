package scw.servlet.session;

import scw.auth.login.Session;
import scw.auth.login.SessionFactory;
import scw.servlet.Request;

/**
 * 此类和AppSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public class WebSession extends AppSession {
	public WebSession(Request request, SessionFactory sessionFactory, String uidKey, String sidKey) {
		super(request, sessionFactory, uidKey, sidKey, true);
	}

	public Session login(String uid) {
		Session session = super.login(uid);
		addCookie();
		return session;
	}
}
