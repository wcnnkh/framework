package io.basc.framework.amqp.boot;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.amqp.MessageProperties;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.value.Values;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.reflect.MethodInvoker;

class MethodInvokerMessageListener<T> implements MessageListener<T> {
	private MethodInvoker invoker;
	private MethodMessageCodec<T> messageCodec;
	private final TypeDescriptor[] typeDescriptors;

	public MethodInvokerMessageListener(MethodInvoker invoker, MethodMessageCodec<T> messageCodec) {
		this.invoker = invoker;
		this.messageCodec = messageCodec;
		List<TypeDescriptor> typeDescriptors = new ArrayList<TypeDescriptor>();
		for (int i = 0; i < invoker.getMethod().getParameterCount(); i++) {
			TypeDescriptor typeDescriptor = new TypeDescriptor(new MethodParameter(invoker.getMethod(), i));
			if (MessageProperties.class == typeDescriptor.getType() || Message.class == typeDescriptor.getType()) {
				// 如果是messageProperties类型，忽略
				continue;
			}
			typeDescriptors.add(typeDescriptor);
		}
		this.typeDescriptors = typeDescriptors.toArray(new TypeDescriptor[0]);
	}

	public void onMessage(String exchange, String routingKey, Message<T> message) throws IOException {
		Type[] types = invoker.getMethod().getGenericParameterTypes();
		Object[] args;
		if (types == null || types.length == 0) {
			args = new Object[0];
		} else {
			args = new Object[invoker.getMethod().getParameterCount()];
			for (int i = 0; i < args.length; i++) {
				TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(types[i]);
				if (MessageProperties.class == typeDescriptor.getType()) {
					args[i] = message;
				}
			}

			Message<Values> invokeMessage = messageCodec.decode(message, typeDescriptors);
			Object[] values = invokeMessage.getBody().toArray();
			int index = 0;
			for (int i = 0; i < values.length; i++) {
				for (int x = index; x < args.length; x++, index++) {
					if (args[x] == null) {
						args[x] = values[i];
					}
				}
			}
		}

		try {
			invoker.invoke(args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return invoker.toString();
	}
}
