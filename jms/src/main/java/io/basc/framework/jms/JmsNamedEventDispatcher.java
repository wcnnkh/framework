package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;

public class JmsNamedEventDispatcher<K, T> implements NamedEventDispatcher<K, T> {
	private final JmsOperations jmsOperations;
	private final MessageSelector<K> messageSelector;
	private final MessageCodec<T> codec;

	public JmsNamedEventDispatcher(JmsOperations jmsOperations, MessageSelector<K> messageSelector,
			MessageCodec<T> codec) {
		Assert.requiredArgument(jmsOperations != null, "sessionOperations");
		Assert.requiredArgument(messageSelector != null, "messageSelector");
		Assert.requiredArgument(codec != null, "codec");
		this.jmsOperations = jmsOperations;
		this.messageSelector = messageSelector;
		this.codec = codec;
	}

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public MessageSelector<K> getMessageSelector() {
		return messageSelector;
	}

	public MessageCodec<T> getCodec() {
		return codec;
	}

	@Override
	public Registration registerListener(K name, EventListener<T> eventListener) throws EventRegistrationException {
		String messageSelector = this.messageSelector.getSelector(name);
		try {
			return jmsOperations.bind(messageSelector, (message) -> {
				T event;
				try {
					event = codec.decode(message);
				} catch (JMSException e) {
					throw new DecodeException(e);
				}
				eventListener.onEvent(event);
			});
		} catch (JMSException e) {
			throw new EventRegistrationException(e);
		}
	}

	@Override
	public void publishEvent(K name, T event) throws EventPushException {
		try {
			MessageBuilder messageBuilder = codec.encode(event);
			jmsOperations.send((session) -> {
				Message message = messageBuilder.build(session);
				messageSelector.write(message, name);
				return message;
			});
		} catch (JMSException e) {
			throw new EventPushException(e);
		}
	}

}
