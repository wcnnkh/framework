package scw.web.model;

import java.io.IOException;
import java.util.Enumeration;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.http.MediaType;
import scw.net.message.Headers;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.convert.WebMessageConverter;
import scw.web.convert.WebMessagelConverterException;

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
	public boolean canWrite(TypeDescriptor type, Object body) {
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
