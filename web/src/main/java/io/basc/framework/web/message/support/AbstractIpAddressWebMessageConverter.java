package io.basc.framework.web.message.support;

import java.io.IOException;
import java.util.List;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractIpAddressWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object ip = request.getIp();
		if (ip == null) {
			return null;
		}
		return getConversionService().convert(ip, TypeDescriptor.forObject(ip),
				parameterDescriptor.getTypeDescriptor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		if (ClassUtils.isMultipleValues(parameterDescriptor.getTypeDescriptor().getType())) {
			List<String> ips = (List<String>) getConversionService().convert(parameter,
					parameterDescriptor.getTypeDescriptor(), TypeDescriptor.collection(List.class, String.class));
			request.getHeaders().put(parameterDescriptor.getName(), ips);
		} else {
			String ip = (String) getConversionService().convert(parameter, parameterDescriptor.getTypeDescriptor(),
					TypeDescriptor.valueOf(String.class));
			request.getHeaders().set(parameterDescriptor.getName(), ip);
		}
		return request;
	}

}
