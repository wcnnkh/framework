package io.basc.framework.mvc;

import io.basc.framework.beans.factory.ParameterFactory;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.server.ServerHttpRequest;
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
		return messageConverters.canRead(request, parameterDescriptor.getTypeDescriptor());
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
