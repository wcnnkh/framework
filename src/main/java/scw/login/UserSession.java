package scw.login;

import scw.session.Session;

public interface UserSession extends Session {
	String getUid();

	UserSessionMetaData getMetaData();
}
