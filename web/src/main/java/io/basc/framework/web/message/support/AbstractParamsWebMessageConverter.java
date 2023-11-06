package io.basc.framework.web.message.support;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.strings.QueryStringConverter;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractParamsWebMessageConverter extends AbstractWebMessageConverter {
	private QueryStringConverter queryStringConverter = QueryStringConverter.getInstance();

	private EntityMapper mapper;

	public EntityMapper getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(EntityMapper mapper) {
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
				parameterDescriptor.getTypeDescriptor());
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
		OutputStreamWriter osw = new OutputStreamWriter(request.getOutputStream(), charset);
		Long writeCount = new Long(bufferingClientHttpRequest.getBufferedOutput().size());
		queryStringConverter.write(parameter, osw);
		return bufferingClientHttpRequest;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		String queryString = QueryStringConverter.getInstance().toQueryString(parameter);
		return builder.query(queryString);
	}
}
