package scw.mvc.action.authority;

import scw.core.instance.annotation.Configuration;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.filter.HttpActionFilter;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;
import scw.util.result.CommonResult;

@Configuration(order = Integer.MIN_VALUE)
public final class HttpActionAuthorityFilter extends HttpActionFilter {
	private final HttpActionAuthorityManager httpActionAuthorityManager;
	private final HttpActionAuthorityIdentify httpActionAuthorityIdentify;

	public HttpActionAuthorityFilter(
			HttpActionAuthorityManager httpActionAuthorityManager,
			HttpActionAuthorityIdentify httpActionAuthorityIdentify) {
		this.httpActionAuthorityManager = httpActionAuthorityManager;
		this.httpActionAuthorityIdentify = httpActionAuthorityIdentify;
	}

	@Override
	protected Object doHttpFilter(HttpChannel channel, HttpAction action,
			ActionFilterChain chain) throws Throwable {
		HttpActionAuthority httpActionAuthority = httpActionAuthorityManager
				.getAuthority(action);
		if (httpActionAuthority == null) {
			return chain.doFilter(channel, action);
		}

		CommonResult<Object> result = httpActionAuthorityIdentify.identify(channel,
				action, httpActionAuthority);
		if (result.isSuccess()) {
			return chain.doFilter(channel, action);
		}
		return result.getData();
	}

}
