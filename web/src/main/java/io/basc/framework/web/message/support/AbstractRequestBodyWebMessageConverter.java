package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractRequestBodyWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object body = WebUtils.getRequestBody(request);
		if (body == null) {
			return null;
		}
		return getConversionService().convert(body, TypeDescriptor.forObject(body),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = request.getContentType();
		if (mediaType != null && !mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED)) {
			getMessageConverter().write(parameterDescriptor.getTypeDescriptor(), parameter, mediaType, request);
			return request;
		}

		// 默认使用表单
		if (mediaType == null) {
			request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			mediaType = MediaType.APPLICATION_FORM_URLENCODED;
		}

		Charset charset = mediaType.getCharset();
		if (charset == null) {
			charset = Constants.UTF_8;
		}

		AbstractBufferingClientHttpRequest bufferingClientHttpRequest = request instanceof AbstractBufferingClientHttpRequest
				? (AbstractBufferingClientHttpRequest) request
				: new BufferingClientHttpRequestWrapper(request);
		if (bufferingClientHttpRequest.getBufferedOutput().size() != 0) {
			bufferingClientHttpRequest.getOutputStream().write("&".getBytes(charset));
		}

		String value = (String) getConversionService().convert(parameter, parameterDescriptor.getTypeDescriptor(),
				TypeDescriptor.valueOf(String.class));
		bufferingClientHttpRequest.getOutputStream().write(parameterDescriptor.getName().getBytes(charset));
		bufferingClientHttpRequest.getOutputStream().write("=".getBytes(charset));
		bufferingClientHttpRequest.getOutputStream().write(URLEncoder.encode(value, charset.name()).getBytes(charset));
		return bufferingClientHttpRequest;
	}

}
