package io.basc.framework.swagger.convert;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;

@Provider(order = Ordered.DEFAULT_PRECEDENCE)
public class SwaggerDefaultValueFactory implements ParameterFactory {
	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return getParameter(parameterDescriptor) != null;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		Schema defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, Schema.class);
		if (defaultValue != null && StringUtils.isNotEmpty(defaultValue.defaultValue())) {
			return defaultValue.defaultValue();
		}
		return null;
	}
}
