package io.basc.framework.amqp.boot;

import io.basc.framework.amqp.Message;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.value.Values;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;

/**
 * 简单粗暴，默认实现
 * 
 * @author wcnnkh
 *
 */
public class SerializerMethodMessageCodec implements MethodMessageCodec<byte[]> {
	public static final SerializerMethodMessageCodec DEFAULT = new SerializerMethodMessageCodec(
			SerializerUtils.getSerializer());

	private Serializer serializer;

	public SerializerMethodMessageCodec(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public Message<Values> decode(Message<byte[]> message, TypeDescriptor[] typeDescriptors) {
		return new Message<Values>(message, Values.of(typeDescriptors, serializer.decode(message.getBody())));
	}

	@Override
	public Message<byte[]> encode(Message<Values> args) {
		return new Message<>(args, serializer.encode(args.getBody().toArray()));
	}
}
