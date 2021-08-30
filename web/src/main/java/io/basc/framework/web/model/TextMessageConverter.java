package io.basc.framework.web.model;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;

@Provider
public class TextMessageConverter implements WebMessageConverter {

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
		return body != null && body instanceof Text;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Text text = (Text) body;
		MimeType mimeType = text.getMimeType();
		if (mimeType == null) {
			mimeType = MimeTypeUtils.TEXT_HTML;
		}
		response.setContentType(mimeType);
		String content = text.toTextContent();
		response.getWriter().write(content);
	}

}
