package scw.auth.login;

public interface SessionFactory {
	Session getSession(String sessionId);

	Session login(String uid);

	Session login(long uid);

	Session login(int uid);

	Session login(String sessionId, String uid);

	void cancelLogin(String sessionId);
}
