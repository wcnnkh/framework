package scw.mvc.action.authority;

import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;
import scw.util.Result;

public interface HttpActionAuthorityIdentify {
	Result<Object> identify(HttpChannel httpChannel, HttpAction httpAction,
			HttpActionAuthority httpActionAuthority) throws Throwable;
}
