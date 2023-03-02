package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;

/**
 * query or form params
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractParamWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object value;
		if (ClassUtils.isMultipleValues(parameterDescriptor.getType())) {
			List<String> values = request.getParameterMap().get(parameterDescriptor.getName());
			if (CollectionUtils.isEmpty(values)) {
				value = getDefaultValue(parameterDescriptor);
			} else {
				value = values;
			}
		} else {
			value = request.getParameterMap().getFirst(parameterDescriptor.getName());
			if (value == null) {
				value = getDefaultValue(parameterDescriptor);
			}
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = request.getContentType();
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

	@SuppressWarnings("unchecked")
	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		if (ClassUtils.isMultipleValues(parameterDescriptor.getType())) {
			List<String> values = (List<String>) getConversionService().convert(parameter,
					new TypeDescriptor(parameterDescriptor), TypeDescriptor.collection(List.class, String.class));
			return builder.queryParam(parameterDescriptor.getName(), values.toArray());
		} else {
			String value = (String) getConversionService().convert(parameter, new TypeDescriptor(parameterDescriptor),
					TypeDescriptor.valueOf(String.class));
			return builder.queryParam(parameterDescriptor.getName(), value);
		}
	}
}
