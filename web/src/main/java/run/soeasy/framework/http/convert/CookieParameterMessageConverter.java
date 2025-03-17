package run.soeasy.framework.http.convert;

import java.io.IOException;
import java.net.HttpCookie;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Parameter;
import run.soeasy.framework.core.execution.ParameterDescriptor;
import run.soeasy.framework.http.HttpMessage;
import run.soeasy.framework.http.server.ServerHttpRequest;
import run.soeasy.framework.http.server.ServerHttpResponse;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.convert.support.AbstractParameterMessageConverter;
import run.soeasy.framework.util.collections.Elements;

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
