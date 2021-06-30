package scw.redis.core.convert;

import scw.convert.Converter;
import scw.redis.core.DefaultMessage;
import scw.redis.core.Message;
import scw.redis.core.MessageListener;

public class ConvertibleMessageListener<TK, TV, K, V> implements MessageListener<K, V> {
	private final MessageListener<TK, TV> messageListener;
	private final Converter<K, TK> keyConverter;
	private final Converter<V, TV> valueConverter;
	
	public ConvertibleMessageListener(MessageListener<TK, TV> messageListener, Converter<K, TK> keyConverter, Converter<V, TV> valueConverter) {
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
