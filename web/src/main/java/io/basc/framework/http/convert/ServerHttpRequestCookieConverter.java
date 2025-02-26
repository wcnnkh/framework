package io.basc.framework.http.convert;

import java.io.IOException;
import java.net.HttpCookie;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.Response;
import io.basc.framework.net.convert.support.AbstractParameterMessageConverter;
import io.basc.framework.web.WebUtils;
import lombok.NonNull;

public class ServerHttpRequestCookieConverter extends AbstractParameterMessageConverter {

	@Override
	public boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message request) {
		return request instanceof ServerHttpRequest;
	}

	@Override
	public Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage request,
			@NonNull Response response) throws IOException {
		ServerHttpRequest serverHttpRequest = (ServerHttpRequest) request;
		HttpCookie httpCookie = WebUtils.getCookie(serverHttpRequest, parameterDescriptor.getName());
		if (httpCookie == null) {
			return null;
		}

		Object value = httpCookie.getValue();
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message response) {
		return response instanceof ServerHttpResponse;
	}

	@Override
	public void doWrite(@NonNull Parameter parameter, @NonNull Request request, @NonNull OutputMessage response)
			throws IOException {
		ServerHttpResponse serverHttpResponse = (ServerHttpResponse) response;
		String value = (String) getConversionService().convert(parameter, TypeDescriptor.valueOf(String.class));
		HttpCookie cookie = new HttpCookie(parameter.getName(), value);
		serverHttpResponse.addCookie(cookie);
	}
}
