package shuchaowen.auth.login;

public interface SessionFactory {
	Session getSession(String sessionId);
	
	Session login(String uid);
	
	void cancelLogin(String sessionId);
}
