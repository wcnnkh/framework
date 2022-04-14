package io.basc.framework.amqp;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.reflect.MethodInvoker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MethodInvokerMessageListener implements MessageListener {
	private MethodInvoker invoker;
	private ArgsMessageCodec messageCodec;
	private final TypeDescriptor[] typeDescriptors;

	public MethodInvokerMessageListener(MethodInvoker invoker,
			ArgsMessageCodec messageCodec) {
		this.invoker = invoker;
		this.messageCodec = messageCodec;
		List<TypeDescriptor> typeDescriptors = new ArrayList<TypeDescriptor>();
		for (int i = 0; i < invoker.getMethod().getParameterCount(); i++) {
			TypeDescriptor typeDescriptor = new TypeDescriptor(
					new MethodParameter(invoker.getMethod(), i));
			if (MessageProperties.class.isAssignableFrom(typeDescriptor
					.getType())) {
				// 如果是messageProperties类型，忽略
				continue;
			}
			typeDescriptors.add(typeDescriptor);
		}
		this.typeDescriptors = typeDescriptors.toArray(new TypeDescriptor[0]);
	}

	public void onMessage(String exchange, String routingKey, Message message)
			throws IOException {
		Class<?>[] types = invoker.getMethod().getParameterTypes();
		Object[] args;
		if (types == null || types.length == 0) {
			args = new Object[0];
		} else {
			args = new Object[invoker.getMethod().getParameterCount()];
			for (int i = 0; i < args.length; i++) {
				Class<?> type = types[i];
				if (MessageProperties.class.isAssignableFrom(type)) {
					args[i] = message;
				}
			}

			Object[] values = messageCodec.decode(message, typeDescriptors);
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
