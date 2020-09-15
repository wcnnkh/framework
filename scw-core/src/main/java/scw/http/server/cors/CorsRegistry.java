package scw.http.server.cors;

import scw.http.server.HttpServiceConfig;
import scw.util.StringMatcher;

/**
 * 跨域路径注册
 * @author shuchaowen
 *
 */
public class CorsRegistry extends HttpServiceConfig<Cors> {

	public CorsRegistry() {
		super();
	}

	public CorsRegistry(StringMatcher matcher) {
		super(matcher);
	}

	@Override
	public HttpServiceConfig<Cors> addMapping(String pattern, Cors cors) {
		Cors corsToUse = cors.isReadyOnly() ? cors : cors.clone().readyOnly();
		super.addMapping(pattern, corsToUse);
		return this;
	}

	@Override
	public HttpServiceConfig<Cors> clear() {
		super.clear();
		return this;
	}
}
