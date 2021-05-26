package scw.web.message.support;

import java.io.IOException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;
import scw.web.message.annotation.RequestBody;

public class RequestBodyMessageConverter implements WebMessageConverter {
	private final ConversionService conversionService;

	public RequestBodyMessageConverter(ConversionService conversionService) {
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
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
	}

}
