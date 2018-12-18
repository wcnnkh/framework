package scw.auth.login.sso;

import scw.auth.login.Session;
import scw.auth.login.SessionFactory;

public interface SSO extends SessionFactory{
	Session getSessionByUid(String uid);
	
	Session getSessionByUid(long uid);
	
	Session getSessionByUid(int uid);
	
	void cancelLoginByUid(String uid);
	
	void cancelLoginByUid(long uid);
	
	void cancelLoginByUid(int uid);
}
