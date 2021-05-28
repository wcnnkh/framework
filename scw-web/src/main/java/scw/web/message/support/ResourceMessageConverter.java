package scw.web.message.support;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.io.Resource;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;

public class ResourceMessageConverter implements WebMessageConverter {

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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
		return body != null && body instanceof Resource;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Resource resource = (Resource) body;
		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		WebUtils.writeStaticResource(request, response, resource, mimeType);
	}

}
