package io.basc.framework.messageing.convert;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.MessageHeaders;

public class MessageConverters extends ConfigurableServices<MessageConverter> implements MessageConverter {

	public MessageConverters() {
		super(MessageConverter.class);
	}

	@Override
	public Object fromMessage(Message<?> message, TypeDescriptor payloadTypeDescriptor) {
		for (MessageConverter converter : getServices()) {
			Object value = converter.fromMessage(message, payloadTypeDescriptor);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public Message<?> toMessage(Object payload, TypeDescriptor payloadTypeDescriptor, MessageHeaders headers) {
		for (MessageConverter converter : getServices()) {
			Message<?> message = converter.toMessage(payload, payloadTypeDescriptor, headers);
			if (message != null) {
				return message;
			}
		}
		return null;
	}

}
