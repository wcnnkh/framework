package io.basc.framework.web.jaxrs2;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.HeaderParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.ConversionMessageConverter;

public class Jaxrs2CookieParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2CookieParamMessageConverter(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(HeaderParam.class)
				&& super.canRead(parameterDescriptor, request);
	}

	@Override
	protected Object readValue(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		HeaderParam headerParam = parameterDescriptor.getAnnotation(HeaderParam.class);
		Object value;
		if (parameterDescriptor.getType().isArray()
				|| Collection.class.isAssignableFrom(parameterDescriptor.getType())) {
			value = request.getHeaders().get(headerParam.value());
		} else {
			value = request.getHeaders().getFirst(headerParam.value());
		}

		if (value == null) {
			value = getDefaultValueFactory().getParameter(parameterDescriptor);
		}
		return value;
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return false;
	}
}
