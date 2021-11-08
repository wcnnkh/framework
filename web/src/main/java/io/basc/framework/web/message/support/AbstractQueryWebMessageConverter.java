package io.basc.framework.web.message.support;

import java.io.IOException;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractQueryWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object value;
		if (XUtils.isMultipleValues(parameterDescriptor.getType())) {
			List<String> values = request.getParameterMap().get(parameterDescriptor.getName());
			if (CollectionUtils.isEmpty(values)) {
				value = getDefaultValue(parameterDescriptor);
			} else {
				value = values;
			}
		} else {
			value = request.getParameterMap().getFirst(parameterDescriptor.getName());
			if (value == null) {
				value = getDefaultValue(parameterDescriptor);
			}
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return false;
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}

	@Override
	public boolean canBuildUri(TypeDescriptor typeDescriptor, Object parameter) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UriComponentsBuilder buildUri(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		if (XUtils.isMultipleValues(parameterDescriptor.getType())) {
			List<String> values = (List<String>) getConversionService().convert(parameter,
					new TypeDescriptor(parameterDescriptor), TypeDescriptor.collection(List.class, String.class));
			return builder.queryParam(parameterDescriptor.getName(), values.toArray());
		} else {
			String value = (String) getConversionService().convert(parameter, new TypeDescriptor(parameterDescriptor),
					TypeDescriptor.valueOf(String.class));
			return builder.queryParam(parameterDescriptor.getName(), value);
		}
	}
}
