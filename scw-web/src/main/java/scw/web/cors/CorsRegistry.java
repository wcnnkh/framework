package scw.web.cors;

import scw.util.Holder;
import scw.web.pattern.HttpPattern;
import scw.web.pattern.HttpPatterns;

/**
 * 跨域路径注册
 * 
 * @author shuchaowen
 *
 */
public class CorsRegistry extends HttpPatterns<Cors> {

	public Holder<Cors> addMapping(String pattern, Cors cors) {
		HttpPattern httpPattern = new HttpPattern(pattern);
		Cors corsToUse = cors.isReadyOnly() ? cors : cors.clone().readyOnly();
		return add(httpPattern, corsToUse);
	}
}
