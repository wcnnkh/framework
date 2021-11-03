package io.basc.framework.mvc.message.support;

import java.io.IOException;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.result.Result;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessageConverterAware;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.mvc.message.annotation.FactoryResult;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class FactoryResultMessageConverter implements WebMessageConverter, WebMessageConverterAware {
	private final ResultFactory resultFactory;
	private WebMessageConverter messageConverter;

	public FactoryResultMessageConverter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	@Override
	public void setWebMessageConverter(WebMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
	
	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		FactoryResult factoryResult = typeDescriptor.getAnnotation(FactoryResult.class);
		return !(Result.class.isAssignableFrom(typeDescriptor.getType())) && factoryResult != null
				&& factoryResult.value();
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		Result result = resultFactory.success(body);
		messageConverter.write(request, response, typeDescriptor.narrow(result), result);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}
}
