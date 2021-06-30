package scw.swagger;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import scw.context.annotation.Provider;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;

@Provider
public class RequestBodyMessageConverter extends scw.web.message.support.RequestBodyMessageConverter {

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(RequestBody.class);
	}
}
