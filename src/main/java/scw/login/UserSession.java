package scw.login;

public interface UserSession extends Session{
	String getUid();
	
	UserSessionMetaData getMetaData();
}
