package io.basc.framework.web.cors;

import io.basc.framework.web.WebException;
import io.basc.framework.web.pattern.WebProcessorRegistry;

/**
 * 跨域路径注册
 * 
 * @author wcnnkh
 *
 */
public class CorsRegistry extends WebProcessorRegistry<Cors, WebException> {
	
	public void add(String pattern, Cors cors) {
		add(pattern, (request) -> Cors.DEFAULT);
	}
}
