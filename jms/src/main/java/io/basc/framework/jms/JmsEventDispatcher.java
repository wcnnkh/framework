package io.basc.framework.jms;

import javax.jms.JMSException;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.unicast.UnicastEventDispatcher;
import io.basc.framework.register.Registration;
import io.basc.framework.util.Assert;

public class JmsEventDispatcher<T>
		implements UnicastEventDispatcher<T>, BroadcastEventDispatcher<T>, EventDispatcher<T> {
	private final JmsOperations jmsOperations;
	private final MessageCodec<T> codec;

	public JmsEventDispatcher(JmsOperations jmsOperations, MessageCodec<T> codec) {
		Assert.requiredArgument(jmsOperations != null, "jmsOperations");
		Assert.requiredArgument(codec != null, "codec");
		this.jmsOperations = jmsOperations;
		this.codec = codec;
	}

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public MessageCodec<T> getCodec() {
		return codec;
	}

	@Override
	public void publishEvent(T event) {
		try {
			MessageBuilder messageBuilder = codec.encode(event);
			jmsOperations.send(messageBuilder);
		} catch (JMSException e) {
			throw new EventPushException(e);
		}
	}

	@Override
	public Registration registerListener(EventListener<T> eventListener) {
		try {
			return jmsOperations.bind((message) -> {
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
}
