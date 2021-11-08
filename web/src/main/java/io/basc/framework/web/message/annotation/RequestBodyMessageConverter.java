package io.basc.framework.web.message.annotation;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.web.message.support.AbstractRequestBodyWebMessageConverter;

public class RequestBodyMessageConverter extends AbstractRequestBodyWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(RequestBody.class);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return typeDescriptor.isAnnotationPresent(RequestBody.class);
	}
}
