package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.io.IOUtils;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class ByteArrayMessageConverter implements WebMessageConverter {

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
		return body != null && (body instanceof byte[] || body instanceof InputStream);
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		if(body == null) {
			return ;
		}

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		if(body instanceof byte[]) {
			response.getOutputStream().write((byte[])body);
			return ;
		}else if(body instanceof InputStream) {
			IOUtils.copy((InputStream)body, response.getOutputStream());
			return ;
		}
		throw new WebMessagelConverterException(type, body, request, null);
	}

}