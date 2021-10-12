package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.Entity;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

public class EntityMessageConverter implements WebMessageConverter {
	private final MessageConverter messageConverter;

	public EntityMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return false;
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return body != null && body instanceof Entity;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Entity<?> entity = (Entity<?>) body;
		InetUtils.writeHeader(entity, response);
		Object entityBody = entity.getBody();
		if (entityBody != null) {
			TypeDescriptor typeDescriptor = TypeDescriptor.forObject(entityBody);
			messageConverter.write(typeDescriptor, typeDescriptor, entity.getContentType(), response);
		}
	}

}
