package io.basc.framework.amqp;

import io.basc.framework.convert.TypeDescriptor;

public interface ArgsMessageCodec {
	byte[] encode(Object... args);

	Object[] decode(Message message, TypeDescriptor... typeDescriptors);
}
