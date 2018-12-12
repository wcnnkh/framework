package shuchaowen.auth.login.sso;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;

public interface SSO extends SessionFactory{
	Session getSessionByUid(String uid);
	
	Session getSessionByUid(long uid);
	
	Session getSessionByUid(int uid);
	
	void cancelLoginByUid(String uid);
	
	void cancelLoginByUid(long uid);
	
	void cancelLoginByUid(int uid);
}
