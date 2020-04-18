package scw.mvc.action.authority;

import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;
import scw.util.result.CommonResult;

public interface HttpActionAuthorityIdentify {
	CommonResult<Object> identify(HttpChannel httpChannel, HttpAction httpAction,
			HttpActionAuthority httpActionAuthority) throws Throwable;
}
