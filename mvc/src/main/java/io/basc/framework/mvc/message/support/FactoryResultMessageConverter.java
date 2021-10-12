package io.basc.framework.mvc.message.support;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.result.Result;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessageConverterAware;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.mvc.message.annotation.FactoryResult;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class FactoryResultMessageConverter implements WebMessageConverter, WebMessageConverterAware {
	private final ResultFactory resultFactory;
	private WebMessageConverter messageConverter;

	public FactoryResultMessageConverter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return false;
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public void setWebMessageConverter(WebMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		if (body == null) {
			return false;
		}

		FactoryResult factoryResult = type.getAnnotation(FactoryResult.class);
		return !(body instanceof Result) && factoryResult != null && factoryResult.value();
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Result result = resultFactory.success(body);
		messageConverter.write(type.narrow(result), result, request, response);
	}
}
