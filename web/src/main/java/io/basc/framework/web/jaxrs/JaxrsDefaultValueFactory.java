package io.basc.framework.web.jaxrs;

import javax.ws.rs.DefaultValue;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.config.support.DefaultValueExecutionInterceptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.ParameterDescriptor;

@Component
class JaxrsDefaultValueFactory extends DefaultValueExecutionInterceptor {

	@Override
	protected Object getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor.getTypeDescriptor(),
				DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}

	@Override
	protected Object getDefaultReturnValue(Executor executor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(executor.getReturnTypeDescriptor(),
				DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}
}
