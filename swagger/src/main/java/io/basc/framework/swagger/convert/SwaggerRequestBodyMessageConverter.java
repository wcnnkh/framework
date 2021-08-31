package io.basc.framework.swagger.convert;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class SwaggerRequestBodyMessageConverter
		extends io.basc.framework.web.message.support.RequestBodyMessageConverter {

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(RequestBody.class);
	}
}