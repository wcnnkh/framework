package scw.login;

public interface LoginFactory {
	UserSessionMetaData getSession(String sessionId);

	UserSessionMetaData login(String uid);

	UserSessionMetaData login(long uid);

	UserSessionMetaData login(int uid);

	UserSessionMetaData login(String sessionId, String uid);

	void cancelLogin(String sessionId);
}
