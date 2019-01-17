package scw.utils.login.sso;

import scw.utils.login.Session;
import scw.utils.login.LoginFactory;

public interface SSO extends LoginFactory{
	Session getSessionByUid(String uid);
	
	Session getSessionByUid(long uid);
	
	Session getSessionByUid(int uid);
	
	void cancelLoginByUid(String uid);
	
	void cancelLoginByUid(long uid);
	
	void cancelLoginByUid(int uid);
}
