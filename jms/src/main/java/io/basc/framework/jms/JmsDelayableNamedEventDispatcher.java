package io.basc.framework.jms;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;

import io.basc.framework.util.event.DelayableNamedEventDispatcher;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.broadcast.BroadcastDelayableNamedEventDispatcher;
import io.basc.framework.util.event.unicast.UnicastDelayableNamedEventDispatcher;

/**
 * jms2.0开始支持延迟消息
 * 
 * @author wcnnkh
 *
 * @param <K> 事件名称类型
 * @param <T> 事件内容类型
 */
public class JmsDelayableNamedEventDispatcher<K, T> extends JmsNamedEventDispatcher<K, T>
		implements UnicastDelayableNamedEventDispatcher<K, T>, BroadcastDelayableNamedEventDispatcher<K, T>,
		DelayableNamedEventDispatcher<K, T> {

	public JmsDelayableNamedEventDispatcher(JmsOperations jmsOperations, MessageSelector<K> messageSelector,
			MessageCodec<T> codec) {
		super(jmsOperations, messageSelector, codec);
	}

	@Override
	public void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		long deliveryTime = System.currentTimeMillis() + delayTimeUnit.toMillis(delay);
		try {
			MessageBuilder messageBuilder = getCodec().encode(event);
			getJmsOperations().send((session) -> {
				Message message = messageBuilder.build(session);
				getMessageSelector().write(message, name);
				message.setJMSDeliveryTime(deliveryTime);
				return message;
			});
		} catch (JMSException e) {
			throw new EventPushException(e);
		}
	}
}
