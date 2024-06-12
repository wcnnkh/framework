package io.basc.framework.messageing.handler.converter;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.convert.MessageConverters;
import io.basc.framework.messageing.handler.HandleMessageConverter;
import io.basc.framework.messageing.handler.HandleMessageConverters;
import lombok.Getter;

@Getter
public class DefaultHandleMessageConverters extends HandleMessageConverters {
	private final MessageConverters payloadMessageConverters = new MessageConverters();

	@Override
	public Object convert(Message<?> message, ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.getTypeDescriptor().getResolvableType().isInstance(message)) {
			return message;
		}

		Payload payload = parameterDescriptor.getTypeDescriptor().getAnnotation(Payload.class);
		if (payload != null) {
			Object body = payloadMessageConverters.fromMessage(message, parameterDescriptor.getTypeDescriptor());
			if (body != null) {
				return body;
			}
		}

		return super.convert(message, parameterDescriptor);
	}

	@Override
	public void configure(Class<HandleMessageConverter> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		payloadMessageConverters.configure(serviceLoaderFactory);
		super.configure(serviceClass, serviceLoaderFactory);
	}
}
