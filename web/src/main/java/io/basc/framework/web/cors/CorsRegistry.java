package io.basc.framework.web.cors;

import io.basc.framework.web.WebException;
import io.basc.framework.web.pattern.ProcessorRegistry;

/**
 * 跨域路径注册
 * 
 * @author shuchaowen
 *
 */
public class CorsRegistry extends ProcessorRegistry<Cors, WebException> {
	
	public void add(String pattern, Cors cors) {
		add(pattern, (request) -> Cors.DEFAULT);
	}
}
