package io.basc.framework.web.jaxrs;

import javax.ws.rs.Path;

import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.type.filter.AnnotationTypeFilter;

@Provider
public class JaxrsContextResolver extends AnnotationTypeFilter implements ContextResolverExtend {

	public JaxrsContextResolver() {
		super(Path.class);
	}
}
