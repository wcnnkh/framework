package io.basc.framework.mvc.jaxrs2;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.HeaderParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.ConversionMessageConverter;

public class Jaxrs2CookieParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2CookieParamMessageConverter(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.isAnnotationPresent(HeaderParam.class) && super.isAccept(parameterDescriptor);
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
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return false;
	}
}
