package scw.web.message.support;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.net.InetUtils;
import scw.net.message.Entity;
import scw.net.message.convert.MessageConverter;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;

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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
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
