package scw.web.model;

import java.io.IOException;

import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;

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
