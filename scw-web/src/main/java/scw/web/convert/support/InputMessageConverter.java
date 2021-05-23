package scw.web.convert.support;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.io.IOUtils;
import scw.net.InetUtils;
import scw.net.message.InputMessage;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.convert.WebMessageConverter;
import scw.web.convert.WebMessagelConverterException;

public class InputMessageConverter implements WebMessageConverter {

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
	public boolean canWrite(TypeDescriptor type, Object body) {
		return body != null && body instanceof InputMessage;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		InputMessage inputMessage = (InputMessage) body;
		InetUtils.writeHeader(inputMessage, response);
		IOUtils.write(inputMessage.getBody(), response.getBody());
	}

}
