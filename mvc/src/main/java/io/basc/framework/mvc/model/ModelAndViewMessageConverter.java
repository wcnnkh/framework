package io.basc.framework.mvc.model;

import java.io.IOException;
import java.util.Enumeration;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.message.Headers;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class ModelAndViewMessageConverter implements WebMessageConverter {
	static final String REQUEST = "_request";

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.getType() == ModelAndView.class;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return new ModelAndView(request.getPath());
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return false;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor, Object body) {
		return body != null && body instanceof ModelAndView && canWrite((ModelAndView) body);
	}

	protected abstract boolean canWrite(ModelAndView page);

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		ModelAndView page = ((ModelAndView) body).clone();
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

		writePage(typeDescriptor, page, request, response);

		if (page.getHttpStatus() != null) {
			response.setStatusCode(page.getHttpStatus());
		}
	}

	protected abstract void writePage(TypeDescriptor type, ModelAndView page, ServerHttpRequest request,
			ServerHttpResponse response) throws IOException, WebMessagelConverterException;
}
