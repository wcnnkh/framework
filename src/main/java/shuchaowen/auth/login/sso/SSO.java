package shuchaowen.auth.login.sso;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;

public interface SSO extends SessionFactory{
	Session getSession(long uid);
	
	void cancelLogin(long uid);
}
