package io.basc.framework.web.message.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractParamsWebMessageConverter extends AbstractWebMessageConverter {
	private ObjectRelationalFactory mapper;

	public ObjectRelationalFactory getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(ObjectRelationalFactory mapper) {
		this.mapper = mapper;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object body = WebUtils.getParameterMap(request, null);
		if (body == null) {
			return null;
		}
		return getConversionService().convert(body, TypeDescriptor.forObject(body),
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
		ObjectRelationalFactory mapping = getMapper();
		ObjectRelational<? extends Property> fields = mapping.getStructure(parameterDescriptor.getType()).all();
		for (Field field : fields.getElements()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			String name = mapping.getName(parameterDescriptor.getType(), field.getGetter());
			Object fieldValue = field.get(parameter);
			String value = (String) getConversionService().convert(fieldValue, new TypeDescriptor(field.getGetter()),
					TypeDescriptor.valueOf(String.class));
			if (bufferingClientHttpRequest.getBufferedOutput().size() != 0) {
				bufferingClientHttpRequest.getOutputStream().write("&".getBytes(charset));
			}
			bufferingClientHttpRequest.getOutputStream().write(name.getBytes(charset));
			bufferingClientHttpRequest.getOutputStream().write("=".getBytes(charset));
			bufferingClientHttpRequest.getOutputStream()
					.write(URLEncoder.encode(value, charset.name()).getBytes(charset));
		}
		return bufferingClientHttpRequest;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		ObjectRelationalFactory mapping = getMapper();
		ObjectRelational<? extends Property> fields = mapping.getStructure(parameterDescriptor.getType()).all();
		for (Field field : fields.getElements()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			String name = mapping.getName(parameterDescriptor.getType(), field.getGetter());
			Object fieldValue = field.get(parameter);
			String value = (String) getConversionService().convert(fieldValue, new TypeDescriptor(field.getGetter()),
					TypeDescriptor.valueOf(String.class));
			builder.queryParam(name, value);
		}
		return builder;
	}
}
