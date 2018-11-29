package shuchaowen.login.sso;

import shuchaowen.login.Session;
import shuchaowen.login.SessionFactory;

public interface SSO extends SessionFactory{
	Session getSession(long uid);
	
	void cancelLogin(long uid);
}
