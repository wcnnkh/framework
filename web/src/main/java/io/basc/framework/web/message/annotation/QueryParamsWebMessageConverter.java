package io.basc.framework.web.message.annotation;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.web.message.support.AbstractParamsWebMessageConverter;

public class QueryParamsWebMessageConverter extends
		AbstractParamsWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(QueryParams.class);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor,
			Object value) {
		return false;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		return typeDescriptor.isAnnotationPresent(QueryParams.class);
	}

}
