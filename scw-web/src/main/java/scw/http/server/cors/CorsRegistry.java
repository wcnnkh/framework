package scw.http.server.cors;

import scw.http.server.pattern.HttpPattern;
import scw.http.server.pattern.HttpPatternRegistry;
import scw.http.server.pattern.HttpPatternRegsitration;

/**
 * 跨域路径注册
 * 
 * @author shuchaowen
 *
 */
public class CorsRegistry extends HttpPatternRegistry<Cors> {

	public HttpPatternRegsitration<Cors> addMapping(String pattern, Cors cors) {
		HttpPattern httpPattern = new HttpPattern(pattern);
		Cors corsToUse = cors.isReadyOnly() ? cors : cors.clone().readyOnly();
		return register(httpPattern, corsToUse);
	}
}
