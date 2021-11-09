package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.net.message.convert.MessageConverterAware;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

/**
 * 应该排在最后一个
 * 
 * @author shuchaowen
 *
 */
public abstract class AbstractWebMessageConverter
		implements WebMessageConverter, ConversionServiceAware, DefaultValueFactoryAware, MessageConverterAware {
	private ConversionService conversionService;
	private ParameterFactory defaultValueFactory;
	private MessageConverter messageConverter;

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public void setDefaultValueFactory(ParameterFactory defaultValueFactory) {
		this.defaultValueFactory = defaultValueFactory;
	}

	@Override
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public ParameterFactory getDefaultValueFactory() {
		return defaultValueFactory;
	}

	public Object getDefaultValue(ParameterDescriptor parameterDescriptor) {
		if (defaultValueFactory == null) {
			return parameterDescriptor.getDefaultValue();
		} else {
			return defaultValueFactory.getParameter(parameterDescriptor);
		}
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return getMessageConverter().read(typeDescriptor, response);
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		if (response.getContentType() == null) {
			// 如果不存在默认使用json返回
			response.setContentType(MediaType.APPLICATION_JSON);
		}
		getMessageConverter().write(typeDescriptor, body, response.getContentType(), response);
	}
}
