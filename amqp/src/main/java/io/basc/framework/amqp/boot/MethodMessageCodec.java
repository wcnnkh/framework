package io.basc.framework.amqp.boot;

import io.basc.framework.amqp.Message;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.value.Values;

public interface MethodMessageCodec<T> {
	Message<T> encode(Message<Values> args);

	Message<Values> decode(Message<T> message, TypeDescriptor[] typeDescriptors);
}
