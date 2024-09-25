package io.basc.framework.jms;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;

import io.basc.framework.util.actor.DelayableEventDispatcher;
import io.basc.framework.util.actor.EventPushException;
import io.basc.framework.util.actor.broadcast.BroadcastDelayableEventDispatcher;
import io.basc.framework.util.actor.unicast.UnicastDelayableEventDispatcher;

/**
 * jms2.0开始支持延迟消息
 * 
 * @author wcnnkh
 *
 * @param <T> 事件内容类型
 */
public class JmsDelayableEventDispatcher<T> extends JmsEventDispatcher<T> implements UnicastDelayableEventDispatcher<T>,
		BroadcastDelayableEventDispatcher<T>, DelayableEventDispatcher<T> {

	public JmsDelayableEventDispatcher(JmsOperations jmsOperations, MessageCodec<T> codec) {
		super(jmsOperations, codec);
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		long deliveryTime = System.currentTimeMillis() + delayTimeUnit.toMillis(delay);
		try {
			MessageBuilder messageBuilder = getCodec().encode(event);
			getJmsOperations().send((session) -> {
				Message message = messageBuilder.build(session);
				message.setJMSDeliveryTime(deliveryTime);
				return message;
			});
		} catch (JMSException e) {
			throw new EventPushException(e);
		}
	}

}
