package io.basc.framework.web.jaxrs;

import javax.ws.rs.DefaultValue;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.config.support.DefaultValueExecutionInterceptor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.execution.Executor;

@Component
public class JaxrsDefaultValueExecutionInterceptor extends DefaultValueExecutionInterceptor {

	@Override
	protected Object getDefaultValue(Executor executor, TypeDescriptor typeDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(typeDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}
}
