package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.HttpCookie;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.ParameterDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractCookieWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		HttpCookie httpCookie = WebUtils.getCookie(request, parameterDescriptor.getName());
		if (httpCookie == null) {
			return null;
		}

		Object value = httpCookie.getValue();
		return getConversionService().convert(value, parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		String value = (String) getConversionService().convert(parameter, parameterDescriptor.getTypeDescriptor(),
				TypeDescriptor.valueOf(String.class));
		HttpCookie cookie = new HttpCookie(parameterDescriptor.getName(), value);
		request.getHeaders().set(HttpHeaders.COOKIE, cookie.toString());
		return request;
	}
}
