package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.annotation.RequestBody;

import java.io.IOException;

public class RequestBodyMessageConverter implements WebMessageConverter, ConversionServiceAware {
	private ConversionService conversionService;
	
	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(RequestBody.class);
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		Object body = WebUtils.getRequestBody(request);
		if (body == null) {
			return null;
		}
		return conversionService.convert(body, TypeDescriptor.forObject(body), new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
	}

}
