package io.basc.framework.mvc.jaxrs2;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.CookieParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.ConversionMessageConverter;

public class Jaxrs2HeaderParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2HeaderParamMessageConverter(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.isAnnotationPresent(CookieParam.class) && super.isAccept(parameterDescriptor);
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
	public boolean isAccept(TypeDescriptor typeDescriptor) {
		return false;
	}
}
