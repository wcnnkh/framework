package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.annotation.QueryParams;

public class QueryParamsMessageConverter extends AbstractWebMessageConverter {
	private ConversionService conversionService;

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.isAnnotationPresent(QueryParams.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object body = WebUtils.getParameterMap(request, null);
		if (body == null) {
			return null;
		}
		return conversionService.convert(body, TypeDescriptor.forObject(body), new TypeDescriptor(parameterDescriptor));
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
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
	}
}
