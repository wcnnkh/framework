package scw.mvc.security;

import scw.env.SystemEnvironment;
import scw.mvc.HttpChannel;

public interface UserSessionResolver{
	static final String TOKEN_NAME = SystemEnvironment.getInstance().getValue(HttpChannel.SESSIONID_ATTRIBUTE,
			String.class, "token");
	static final String UID_NAME = SystemEnvironment.getInstance().getValue(HttpChannel.UID_ATTRIBUTE, String.class,
			"uid");
	
	<T> T getUid(HttpChannel httpChannel, Class<T> type);
	
	String getSessionId(HttpChannel httpChannel);
}
