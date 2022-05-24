package io.basc.framework.redis.convert;

import java.util.function.Function;

import io.basc.framework.redis.DefaultMessage;
import io.basc.framework.redis.Message;
import io.basc.framework.redis.MessageListener;

public class ConvertibleMessageListener<TK, TV, K, V> implements MessageListener<K, V> {
	private final MessageListener<TK, TV> messageListener;
	private final Function<K, TK> keyConverter;
	private final Function<V, TV> valueConverter;

	public ConvertibleMessageListener(MessageListener<TK, TV> messageListener, Function<K, TK> keyConverter,
			Function<V, TV> valueConverter) {
		this.messageListener = messageListener;
		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
	}

	@Override
	public void onMessage(Message<K, V> message, K pattern) {
		Message<TK, TV> msg = new DefaultMessage<TK, TV>(keyConverter.apply(message.getChannel()),
				valueConverter.apply(message.getBody()));
		messageListener.onMessage(msg, keyConverter.apply(pattern));
	}

}
