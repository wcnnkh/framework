package io.basc.framework.http.convert;

import java.io.IOException;
import java.net.HttpCookie;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.support.AbstractParameterMessageConverter;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class CookieParameterMessageConverter extends AbstractParameterMessageConverter {

	public static Elements<HttpCookie> getHttpCookies(@NonNull String name, @NonNull HttpMessage httpMessage) {
		if (httpMessage instanceof ServerHttpRequest) {
			return ((ServerHttpRequest) httpMessage).getCookies().filter((e) -> name.equals(e.getName()));
		}
		return httpMessage.getHeaders().getCookies().filter((e) -> name.equals(e.getName()));
	}

	public static void addHttpCookie(HttpCookie httpCookie, HttpMessage httpMessage) {
		if (httpMessage instanceof ServerHttpResponse) {
			((ServerHttpResponse) httpMessage).addCookie(httpCookie);
		} else {
			httpMessage.getHeaders().addCookie(httpCookie);
		}
	}

	@Override
	protected boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message request) {
		return request instanceof HttpMessage;
	}

	@Override
	protected Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage message)
			throws IOException {
		HttpMessage httpMessage = (HttpMessage) message;
		HttpCookie httpCookie = getHttpCookies(parameterDescriptor.getName(), httpMessage).first();
		Object value = httpCookie.getValue();
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	protected boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message response) {
		return !response.getHeaders().isReadyOnly() && response instanceof HttpMessage;
	}

	@Override
	protected void doWrite(@NonNull Parameter parameter, @NonNull OutputMessage message) throws IOException {
		HttpMessage httpMessage = (HttpMessage) message;
		String value = (String) getConversionService().convert(parameter, TypeDescriptor.valueOf(String.class));
		HttpCookie cookie = new HttpCookie(parameter.getName(), value);
		addHttpCookie(cookie, httpMessage);
	}
}
