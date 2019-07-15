package scw.login.sso;

import scw.login.LoginFactory;
import scw.login.Session;

public interface SSO extends LoginFactory{
	Session getSessionByUid(String uid);
	
	Session getSessionByUid(long uid);
	
	Session getSessionByUid(int uid);
	
	void cancelLoginByUid(String uid);
	
	void cancelLoginByUid(long uid);
	
	void cancelLoginByUid(int uid);
}
