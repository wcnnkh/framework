package io.basc.framework.mvc.jaxrs2;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.CookieParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.mvc.message.support.ConversionMessageConverter;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class Jaxrs2HeaderParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2HeaderParamMessageConverter(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(CookieParam.class)
				&& super.canRead(parameterDescriptor, request);
	}

	@Override
	protected Object readValue(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		CookieParam cookieParam = parameterDescriptor.getAnnotation(CookieParam.class);
		Object value;
		if (parameterDescriptor.getType().isArray()
				|| Collection.class.isAssignableFrom(parameterDescriptor.getType())) {
			value = request.getHeaders().get(cookieParam.value());
		} else {
			value = request.getHeaders().getFirst(cookieParam.value());
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
