package scw.mvc.action.authority.filter;

import scw.core.instance.annotation.Configuration;
import scw.mvc.action.authority.HttpActionAuthority;
import scw.mvc.action.authority.HttpActionAuthorityManager;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.filter.HttpActionFilter;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;

@Configuration
public class HttpActionAuthorityFilter extends HttpActionFilter {
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

		if (httpActionAuthorityIdentify.identify(channel, action,
				httpActionAuthority)) {
			return chain.doFilter(channel, action);
		}

		return httpActionAuthorityIdentify.error(channel, action);
	}

}
