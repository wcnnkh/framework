package io.basc.framework.web.jaxrs;

import java.io.IOException;

import javax.ws.rs.CookieParam;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.AbstractCookieWebMessageConverter;

public class JaxrsCookieParamMessageConverter extends AbstractCookieWebMessageConverter {
	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(CookieParam.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		CookieParam headerParam = parameterDescriptor.getTypeDescriptor().getAnnotation(CookieParam.class);
		if (headerParam == null || StringUtils.isEmpty(headerParam.value())) {
			return super.read(request, parameterDescriptor);
		}
		return super.read(request, parameterDescriptor.rename(headerParam.value()));
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return typeDescriptor.isAnnotationPresent(CookieParam.class);
	}
}
