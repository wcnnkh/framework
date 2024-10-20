package io.basc.framework.web.message.support;

import java.io.IOException;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractHeaderWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object value;
		if (ClassUtils.isMultipleValues(parameterDescriptor.getType())) {
			value = request.getHeaders().get(parameterDescriptor.getName());
		} else {
			value = request.getHeaders().getFirst(parameterDescriptor.getName());
		}

		if (value == null) {
			value = getDefaultValue(parameterDescriptor);
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				new TypeDescriptor(parameterDescriptor));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		if (ClassUtils.isMultipleValues(parameterDescriptor.getType())) {
			List<String> values = (List<String>) getConversionService().convert(parameter,
					new TypeDescriptor(parameterDescriptor), TypeDescriptor.collection(List.class, String.class));
			request.getHeaders().put(parameterDescriptor.getName(), values);
		} else {
			String value = (String) getConversionService().convert(parameter, new TypeDescriptor(parameterDescriptor),
					TypeDescriptor.valueOf(String.class));
			request.getHeaders().add(parameterDescriptor.getName(), value);
		}
		return request;
	}
}
