package io.basc.framework.redis.convert;

import io.basc.framework.convert.Converter;
import io.basc.framework.redis.DefaultMessage;
import io.basc.framework.redis.Message;
import io.basc.framework.redis.MessageListener;

public class ConvertibleMessageListener<TK, TV, K, V> implements MessageListener<K, V> {
	private final MessageListener<TK, TV> messageListener;
	private final Converter<K, TK> keyConverter;
	private final Converter<V, TV> valueConverter;

	public ConvertibleMessageListener(MessageListener<TK, TV> messageListener, Converter<K, TK> keyConverter,
			Converter<V, TV> valueConverter) {
		this.messageListener = messageListener;
		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
	}

	@Override
	public void onMessage(Message<K, V> message, K pattern) {
		Message<TK, TV> msg = new DefaultMessage<TK, TV>(keyConverter.convert(message.getChannel()),
				valueConverter.convert(message.getBody()));
		messageListener.onMessage(msg, keyConverter.convert(pattern));
	}

}
