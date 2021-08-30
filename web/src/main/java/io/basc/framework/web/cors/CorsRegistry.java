package io.basc.framework.web.cors;

import io.basc.framework.util.Holder;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatterns;

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
