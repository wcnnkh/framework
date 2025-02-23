package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import io.basc.framework.core.Constants;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.net.MediaType;
import io.basc.framework.util.comparator.Ordered;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

/**
 * 应该排在最后一个
 * 
 * @author wcnnkh
 *
 */
class LastWebMessageConverter extends AbstractWebMessageConverter implements Ordered {

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return getConversionService().canConvert(TypeDescriptor.valueOf(String.class), descriptor)
				|| getConversionService().canConvert(TypeDescriptor.collection(List.class, String.class), descriptor);
	}

	protected Object readValue(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		Object source;
		if (parameterDescriptor.getClass().isArray()) {
			source = WebUtils.getParameterValues(request, parameterDescriptor.getName());
		} else {
			source = WebUtils.getParameter(request, parameterDescriptor.getName());
		}
		return source;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object source = readValue(parameterDescriptor, request);
		return getConversionService().convert(source, TypeDescriptor.forObject(source),
				parameterDescriptor.getTypeDescriptor());
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return true;
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = request.getContentType();
		if (mediaType != null && !mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED)) {
			getMessageConverter().writeTo(, mediaType, request);
			getMessageConverter().writeTo(parameterDescriptor.getTypeDescriptor(), parameter, mediaType, request);
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
				: new BufferingClientHttpRequestWrapper<>(request);
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
