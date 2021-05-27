package scw.web.cors;

import scw.web.pattern.HttpPattern;
import scw.web.pattern.HttpPatternRegistry;
import scw.web.pattern.HttpPatternRegsitration;

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
