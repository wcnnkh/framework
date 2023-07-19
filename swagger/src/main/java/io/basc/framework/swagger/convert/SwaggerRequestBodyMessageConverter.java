package io.basc.framework.swagger.convert;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.web.message.support.AbstractRequestBodyWebMessageConverter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class SwaggerRequestBodyMessageConverter extends AbstractRequestBodyWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(RequestBody.class);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return typeDescriptor.isAnnotationPresent(RequestBody.class);
	}

}
