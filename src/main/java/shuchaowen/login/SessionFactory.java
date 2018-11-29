package shuchaowen.login;

public interface SessionFactory {
	Session getSession(String sessionId);
	
	Session login(long uid);
	
	void cancelLogin(String sessionId);
}
