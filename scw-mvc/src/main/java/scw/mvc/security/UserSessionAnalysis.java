package scw.mvc.security;

import scw.mvc.HttpChannel;

public interface UserSessionAnalysis{
	<T> T getUid(HttpChannel httpChannel, Class<T> type);
	
	String getSessionId(HttpChannel httpChannel);
}
