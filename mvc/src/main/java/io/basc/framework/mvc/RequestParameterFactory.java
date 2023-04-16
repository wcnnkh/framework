package io.basc.framework.mvc;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ParameterFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.WebMessagelConverterException;

public class RequestParameterFactory implements ParameterFactory {
	private final ServerHttpRequest request;
	private final WebMessageConverters messageConverters = new WebMessageConverters();

	public RequestParameterFactory(ServerHttpRequest request, WebMessageConverter messageConverter) {
		this.request = request;
		messageConverters.registerLast(messageConverter);
	}

	public ServerHttpRequest getRequest() {
		return request;
	}

	public WebMessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return messageConverters.canRead(request, new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		try {
			return messageConverters.read(request, parameterDescriptor);
		} catch (Exception e) {
			throw new WebMessagelConverterException(parameterDescriptor, request, e);
		}
	}
}
