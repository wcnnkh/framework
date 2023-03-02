package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
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
			Value[] values = WebUtils.getParameterValues(request, parameterDescriptor.getName());
			if (ArrayUtils.isEmpty(values)) {
				source = getDefaultValue(parameterDescriptor);
			} else {
				source = values;
			}
		} else {
			Value value = WebUtils.getParameter(request, parameterDescriptor.getName());
			if (value == null || value.isEmpty()) {
				source = getDefaultValue(parameterDescriptor);
			} else {
				source = value;
			}
		}
		return source;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object source = readValue(parameterDescriptor, request);
		return getConversionService().convert(source, TypeDescriptor.forObject(source),
				new TypeDescriptor(parameterDescriptor));
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
			getMessageConverter().write(new TypeDescriptor(parameterDescriptor), parameter, mediaType, request);
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

		String value = (String) getConversionService().convert(parameter, new TypeDescriptor(parameterDescriptor),
				TypeDescriptor.valueOf(String.class));
		bufferingClientHttpRequest.getOutputStream().write(parameterDescriptor.getName().getBytes(charset));
		bufferingClientHttpRequest.getOutputStream().write("=".getBytes(charset));
		bufferingClientHttpRequest.getOutputStream().write(URLEncoder.encode(value, charset.name()).getBytes(charset));
		return bufferingClientHttpRequest;
	}
}
