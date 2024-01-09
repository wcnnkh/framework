package io.basc.framework.web.message.support;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.net.message.multipart.MultipartMessageConverter;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
import io.basc.framework.value.ParameterDescriptor;
import io.basc.framework.web.MultiPartServerHttpRequest;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class MultipartMessageWebMessageConverter extends MultipartMessageConverter implements WebMessageConverter {

	public MultipartMessageWebMessageConverter(@Nullable MultipartMessageResolver multipartMessageResolver) {
		super(multipartMessageResolver);
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		if (message instanceof MultiPartServerHttpRequest) {
			return true;
		}
		return getMultipartMessageResolver() != null && canReadType(descriptor);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Collection<MultipartMessage> messages;
		if (request instanceof MultiPartServerHttpRequest) {
			messages = ((MultiPartServerHttpRequest) request).getMultipartMessages();
		} else {
			messages = getMultipartMessageResolver().resolve(request);
		}
		return convert(messages, parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		List<MultipartMessage> messages = getMultipartMessageResolver().resolve(response);
		return convert(messages, typeDescriptor);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return false;
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}
}
