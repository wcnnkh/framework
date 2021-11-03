package io.basc.framework.mvc.message;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.web.ServerHttpRequest;

public class RequestParameterFactory implements ParameterFactory {
	private final ServerHttpRequest request;
	private final WebMessageConverters messageConverters = new WebMessageConverters();

	public RequestParameterFactory(ServerHttpRequest request, WebMessageConverter messageConverter) {
		this.request = request;
		messageConverters.setAfterService(messageConverter);
	}

	public ServerHttpRequest getRequest() {
		return request;
	}

	public WebMessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return messageConverters.isAccept(parameterDescriptor);
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
