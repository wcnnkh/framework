package io.basc.framework.web.message.support;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.io.Resource;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;

public class ResourceMessageConverter extends AbstractWebMessageConverter {

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		Resource resource = (Resource) body;
		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		WebUtils.writeStaticResource(request, response, resource, mimeType);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return false;
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return Resource.class.isAssignableFrom(typeDescriptor.getType());
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}
}
