package io.basc.framework.web.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Resource;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;

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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
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
