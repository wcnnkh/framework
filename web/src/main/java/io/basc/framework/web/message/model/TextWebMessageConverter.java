package io.basc.framework.web.message.model;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.AbstractWebMessageConverter;

import java.io.IOException;
import java.nio.charset.Charset;

@Provider
public class TextWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor,
			Object value) {
		return Text.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response,
			TypeDescriptor typeDescriptor, Object body) throws IOException,
			WebMessagelConverterException {
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
	public ClientHttpRequest write(ClientHttpRequest request,
			ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		Text text = (Text) parameter;
		MimeType mimeType = text.getMimeType();
		if (mimeType == null) {
			mimeType = MimeTypeUtils.TEXT_HTML;
		}

		Charset charset = mimeType.getCharset();
		if (charset == null) {
			charset = Constants.UTF_8;
		}

		request.setContentType(mimeType);
		String content = text.toTextContent();
		request.getOutputStream().write(content.getBytes(charset));
		return request;
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.getType().isAssignableFrom(Text.class);
	}

	@Override
	public Object read(ServerHttpRequest request,
			ParameterDescriptor parameterDescriptor) throws IOException,
			WebMessagelConverterException {
		String content = request.getString();
		return new Text() {

			@Override
			public String toTextContent() {
				return content;
			}

			@Override
			public MimeType getMimeType() {
				return request.getContentType();
			}
		};
	}
}
