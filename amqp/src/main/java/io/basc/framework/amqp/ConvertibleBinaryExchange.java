package io.basc.framework.amqp;

import io.basc.framework.util.codec.Codec;

public class ConvertibleBinaryExchange<S> extends ConvertibleExchange<S, byte[]> implements BinaryExchange {

	public ConvertibleBinaryExchange(Exchange<S> sourceExchange, Codec<Message<S>, Message<byte[]>> codec) {
		super(sourceExchange, codec);
	}

}
