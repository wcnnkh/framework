package io.basc.framework.mvc.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.Entity;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class EntityMessageConverter implements WebMessageConverter {
	private final MessageConverter messageConverter;

	public EntityMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}
	
	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return Entity.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor type, Object body)
			throws IOException, WebMessagelConverterException {
		Entity<?> entity = (Entity<?>) body;
		InetUtils.writeHeader(entity, response);
		Object entityBody = entity.getBody();
		if (entityBody != null) {
			TypeDescriptor typeDescriptor = TypeDescriptor.forObject(entityBody);
			messageConverter.write(typeDescriptor, typeDescriptor, entity.getContentType(), response);
		}
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

}
