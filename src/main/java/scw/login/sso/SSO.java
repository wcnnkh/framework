package scw.login.sso;

import scw.login.LoginFactory;
import scw.login.UserSessionMetaData;

public interface SSO extends LoginFactory{
	UserSessionMetaData getSessionByUid(String uid);
	
	UserSessionMetaData getSessionByUid(long uid);
	
	UserSessionMetaData getSessionByUid(int uid);
	
	void cancelLoginByUid(String uid);
	
	void cancelLoginByUid(long uid);
	
	void cancelLoginByUid(int uid);
}
