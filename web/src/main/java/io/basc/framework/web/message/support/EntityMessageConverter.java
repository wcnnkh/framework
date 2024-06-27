package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.http.HttpEntity;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.Entity;
import io.basc.framework.net.InetUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;

public class EntityMessageConverter extends AbstractWebMessageConverter {
	private static Logger logger = LoggerFactory.getLogger(EntityMessageConverter.class);

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.getType().isAssignableFrom(HttpEntity.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor();
		if (typeDescriptor.isGeneric()) {
			typeDescriptor = typeDescriptor.getNested(1);
		} else {
			typeDescriptor = TypeDescriptor.valueOf(Object.class);
		}
		Object value = getMessageConverter().read(typeDescriptor, request);
		return new HttpRequestEntity<Object>(value, request.getHeaders(), request.getRawMethod(), request.getURI(),
				typeDescriptor);
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		Object value;
		if (typeDescriptor.isGeneric()) {
			try {
				value = response.getBytes();
			} catch (IOException e) {
				logger.error(e, response.toString());
				return null;
			}
		} else {
			value = getMessageConverter().read(typeDescriptor.getNested(0), response);
		}
		return new HttpResponseEntity<Object>(value, typeDescriptor, response.getHeaders(), response.getStatusCode());
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
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
			getMessageConverter().write(typeDescriptor, typeDescriptor, entity.getContentType(), response);
		}
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		getMessageConverter().write(parameterDescriptor.getTypeDescriptor(), parameter, request.getContentType(),
				request);
		return request;
	}
}
