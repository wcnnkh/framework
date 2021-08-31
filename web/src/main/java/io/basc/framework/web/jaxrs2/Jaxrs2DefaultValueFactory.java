package io.basc.framework.web.jaxrs2;

import javax.ws.rs.DefaultValue;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class Jaxrs2DefaultValueFactory implements ParameterFactory {

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return AnnotatedElementUtils.hasAnnotation(parameterDescriptor, DefaultValue.class);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, DefaultValue.class);
		return defaultValue == null ? null : defaultValue.value();
	}
}
