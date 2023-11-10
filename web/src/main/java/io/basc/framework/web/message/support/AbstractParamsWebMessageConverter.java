package io.basc.framework.web.message.support;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.LongAdder;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.AbstractBufferingClientHttpRequest;
import io.basc.framework.http.client.BufferingClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.lang.Constants;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.text.QueryStringFormat;
import io.basc.framework.util.Assert;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

public abstract class AbstractParamsWebMessageConverter extends AbstractWebMessageConverter {
	private QueryStringFormat queryStringConverter = new QueryStringFormat();

	@Override
	public void setConversionService(ConversionService conversionService) {
		queryStringConverter.setConversionService(conversionService);
		super.setConversionService(conversionService);
	}

	public QueryStringFormat getQueryStringConverter() {
		return queryStringConverter;
	}

	public void setQueryStringConverter(QueryStringFormat queryStringConverter) {
		Assert.requiredArgument(queryStringConverter != null, "queryStringConverter");
		this.queryStringConverter = queryStringConverter;
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
		LongAdder writtenSize = new LongAdder();
		writtenSize.add(bufferingClientHttpRequest.getBufferedOutput().size());
		queryStringConverter.write(writtenSize, parameter, osw);
		return bufferingClientHttpRequest;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		String queryString = queryStringConverter.toQueryString(parameter);
		return builder.query(queryString);
	}
}
