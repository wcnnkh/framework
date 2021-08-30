package io.basc.framework.web.model;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.message.Headers;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;
import java.util.Enumeration;

public abstract class PageMessageConverter implements WebMessageConverter {
	static final String REQUEST = "_request";

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
		return body != null && body instanceof Page && canWrite((Page) body);
	}
	
	protected abstract boolean canWrite(Page page);

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Page page = ((Page) body).clone();
		if (!page.containsKey(REQUEST)) {
			page.put(REQUEST, request);
		}

		Headers headers = page.getHeaders();
		if (headers != null) {
			response.getHeaders().putAll(headers);
		}
		
		if (response.getContentType() == null) {
			response.setContentType(MediaType.TEXT_HTML);
		}

		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object value = request.getAttribute(name);
			if (value != null) {
				page.put(name, value);
			}
		}

		writePage(type, page, request, response);
	}

	protected abstract void writePage(TypeDescriptor type, Page page, ServerHttpRequest request,
			ServerHttpResponse response) throws IOException, WebMessagelConverterException;
}
