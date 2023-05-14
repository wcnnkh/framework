package io.basc.framework.web.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponents;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractPathParamWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Map<String, String> valueMap = WebUtils.getRestfulParameterMap(request);
		Object value = null;
		if (valueMap != null) {
			value = valueMap.get(parameterDescriptor.getName());
		}

		if (value == null) {
			value = getDefaultValue(parameterDescriptor);
		}

		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public final ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor,
			Object parameter) throws IOException, WebMessagelConverterException {
		return request;
	}

	@Override
	public final boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return false;
	}

	@Override
	public abstract boolean canWrite(TypeDescriptor typeDescriptor, Object parameter);

	@SuppressWarnings("unchecked")
	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		Map<String, String> uriVariables;
		if (parameterDescriptor.getTypeDescriptor().isMap()) {
			uriVariables = (Map<String, String>) getConversionService().convert(parameter,
					parameterDescriptor.getTypeDescriptor(), TypeDescriptor.map(Map.class, String.class, String.class));
		} else {
			String value = (String) getConversionService().convert(parameter, parameterDescriptor.getTypeDescriptor(),
					TypeDescriptor.valueOf(String.class));
			uriVariables = Collections.singletonMap(parameterDescriptor.getName(), value);
		}
		UriComponents uriComponents = builder.buildAndExpand(uriVariables);
		return UriComponentsBuilder.fromUri(uriComponents.toUri());
	}
}
