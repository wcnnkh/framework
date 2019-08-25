package scw.login;

public interface UserSession extends Session1{
	String getUid();
	
	UserSessionMetaData getMetaData();
}
