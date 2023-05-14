package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.HttpCookie;

import javax.ws.rs.core.Cookie;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractCookieWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		HttpCookie httpCookie = WebUtils.getCookie(request, parameterDescriptor.getName());
		Object value;
		if (httpCookie == null) {
			value = getDefaultValue(parameterDescriptor);
		} else {
			value = httpCookie.getValue();
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		String value = (String) getConversionService().convert(parameter, parameterDescriptor.getTypeDescriptor(),
				TypeDescriptor.valueOf(String.class));
		Cookie cookie = new Cookie(parameterDescriptor.getName(), value);
		request.getHeaders().set(HttpHeaders.COOKIE, cookie.toString());
		return request;
	}
}
