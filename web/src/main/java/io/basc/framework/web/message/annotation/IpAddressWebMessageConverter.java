package io.basc.framework.web.message.annotation;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.web.message.support.AbstractIpAddressWebMessageConverter;

public class IpAddressWebMessageConverter extends AbstractIpAddressWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.hasAnnotation(IP.class);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return typeDescriptor.isAnnotationPresent(IP.class);
	}

}
