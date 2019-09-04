package scw.mvc.http.session;

import scw.login.LoginFactory;
import scw.login.UserSessionMetaData;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpResponse;

/**
 * 此类和AppSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public class WebSession extends AppSession {
	public WebSession(HttpChannel channel, LoginFactory loginFactory, String uidKey, String sidKey) {
		super(channel, loginFactory, uidKey, sidKey, true);
	}

	public UserSessionMetaData login(HttpResponse httpResponse, String uid) {
		UserSessionMetaData userSessionMetaData = super.login(uid);
		addCookie(httpResponse);
		return userSessionMetaData;
	}
}
