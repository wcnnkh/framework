package io.basc.framework.amqp.support;

import io.basc.framework.amqp.ArgsMessageCodec;
import io.basc.framework.amqp.Message;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;

/**
 * 简单粗暴，默认实现
 * 
 * @author wcnnkh
 *
 */
public class SerializerArgsMessageCodec implements ArgsMessageCodec {
	private Serializer serializer;

	public SerializerArgsMessageCodec() {
		this(SerializerUtils.getSerializer());
	}

	public SerializerArgsMessageCodec(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public byte[] encode(Object... args) {
		return serializer.encode(args);
	}

	@Override
	public Object[] decode(Message message, TypeDescriptor... typeDescriptors) {
		return (Object[]) serializer.decode(message.getBody());
	}
}
