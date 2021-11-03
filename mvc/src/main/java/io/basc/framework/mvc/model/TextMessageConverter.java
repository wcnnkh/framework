package io.basc.framework.mvc.model;

import java.io.IOException;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

@Provider
public class TextMessageConverter implements WebMessageConverter {

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return Text.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		Text text = (Text) body;
		MimeType mimeType = text.getMimeType();
		if (mimeType == null) {
			mimeType = MimeTypeUtils.TEXT_HTML;
		}
		response.setContentType(mimeType);
		String content = text.toTextContent();
		response.getWriter().write(content);
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
