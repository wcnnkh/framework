package io.basc.framework.web.jaxrs;

import javax.ws.rs.DefaultValue;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.config.support.DefaultValueExecutionInterceptor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.ParameterDescriptor;

@Component
public class JaxrsDefaultValueExecutionInterceptor extends DefaultValueExecutionInterceptor {

	@Override
	protected Object getDefaultParameterValue(ParameterDescriptor parameterDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor.getTypeDescriptor(),
				DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}

	@Override
	protected Object getDefaultReturnValue(TypeDescriptor returnTypeDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(returnTypeDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}
}
