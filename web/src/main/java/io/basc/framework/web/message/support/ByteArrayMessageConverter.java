package io.basc.framework.web.message.support;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.io.IOUtils;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class ByteArrayMessageConverter implements WebMessageConverter {
	@Override
	public boolean isAccept(TypeDescriptor typeDescriptor) {
		return typeDescriptor.getType() == byte[].class || InputStream.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException {
		if (body == null) {
			return;
		}

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		if (body instanceof byte[]) {
			response.getOutputStream().write((byte[]) body);
			return;
		} else if (body instanceof InputStream) {
			IOUtils.copy((InputStream) body, response.getOutputStream());
			return;
		}
		throw new WebMessagelConverterException(typeDescriptor, body, request, null);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}
}
