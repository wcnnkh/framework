package scw.web.message.support;

import java.io.IOException;
import java.io.InputStream;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.http.MediaType;
import scw.io.IOUtils;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;

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
