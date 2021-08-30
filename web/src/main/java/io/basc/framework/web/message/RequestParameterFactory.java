package io.basc.framework.web.message;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.web.ServerHttpRequest;

public class RequestParameterFactory implements ParameterFactory {
	private final ServerHttpRequest request;
	private final WebMessageConverters messageConverters;

	public RequestParameterFactory(ServerHttpRequest request, WebMessageConverter messageConverter) {
		this.request = request;
		this.messageConverters = new WebMessageConverters(messageConverter);
	}

	public ServerHttpRequest getRequest() {
		return request;
	}

	public WebMessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return messageConverters.canRead(parameterDescriptor, request);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		try {
			return messageConverters.read(parameterDescriptor, request);
		} catch (Exception e) {
			throw new WebMessagelConverterException(parameterDescriptor, request, e);
		}
	}
}
