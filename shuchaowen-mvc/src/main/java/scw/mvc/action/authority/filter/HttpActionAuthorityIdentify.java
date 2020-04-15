package scw.mvc.action.authority.filter;

import scw.mvc.action.authority.HttpActionAuthority;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;

public interface HttpActionAuthorityIdentify {
	boolean identify(HttpChannel httpChannel, HttpAction httpAction,
			HttpActionAuthority httpActionAuthority);
	
	Object error(HttpChannel channel, HttpAction httpAction);
}
