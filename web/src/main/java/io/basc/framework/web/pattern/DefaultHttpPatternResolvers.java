package io.basc.framework.web.pattern;

import io.basc.framework.web.pattern.annotation.AnnotationHttpPatternResolver;

public class DefaultHttpPatternResolvers extends HttpPatternResolvers {

	public DefaultHttpPatternResolvers() {
		registerService(new AnnotationHttpPatternResolver());
	}
}
