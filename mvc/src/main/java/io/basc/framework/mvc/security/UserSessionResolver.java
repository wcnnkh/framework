package io.basc.framework.mvc.security;

import io.basc.framework.env.Sys;
import io.basc.framework.mvc.HttpChannel;

public interface UserSessionResolver{
	static final String TOKEN_NAME = Sys.env.getValue(HttpChannel.SESSIONID_ATTRIBUTE,
			String.class, "token");
	static final String UID_NAME = Sys.env.getValue(HttpChannel.UID_ATTRIBUTE, String.class,
			"uid");
	
	<T> T getUid(HttpChannel httpChannel, Class<T> type);
	
	String getSessionId(HttpChannel httpChannel);
}
