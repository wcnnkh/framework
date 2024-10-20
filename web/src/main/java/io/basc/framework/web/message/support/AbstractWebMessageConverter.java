package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.DefaultParameterFactory;
import io.basc.framework.factory.DefaultParameterFactoryAware;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.net.message.convert.MessageConverterAware;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

/**
 * 应该排在最后一个
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractWebMessageConverter
		implements WebMessageConverter, ConversionServiceAware, DefaultParameterFactoryAware, MessageConverterAware {
	private ConversionService conversionService;
	private DefaultParameterFactory defaultParameterFactory;
	private MessageConverter messageConverter;

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public DefaultParameterFactory getDefaultParameterFactory() {
		return defaultParameterFactory;
	}

	public Object getDefaultValue(ParameterDescriptor parameterDescriptor) {
		DefaultParameterFactory defaultParameterFactory = getDefaultParameterFactory();
		return defaultParameterFactory == null ? null
				: defaultParameterFactory.getDefaultParameter(parameterDescriptor);
	}

	@Override
	public void setDefaultParameterFactory(DefaultParameterFactory defaultParameterFactory) {
		this.defaultParameterFactory = defaultParameterFactory;
	}

	@Override
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public ConversionService getConversionService() {
		return conversionService;
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
			if (Value.isBaseType(typeDescriptor.getType())) {
				response.setContentType(MediaType.TEXT_HTML);
			} else {
				response.setContentType(MediaType.APPLICATION_JSON);
			}
		}
		getMessageConverter().write(typeDescriptor, body, response.getContentType(), response);
	}
}
