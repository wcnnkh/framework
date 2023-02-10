package io.basc.framework.jms;

import java.lang.reflect.Method;

import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

public interface JmsSupplier {
	@Nullable
	<T> MessageConsumer getMessageConsumer(Class<? extends T> clazz);

	@Nullable
	MessageConsumer getMessageConsumer(Class<?> clazz, Method method);

	default <T> MessageListener getMessageListener(Class<? extends T> clazz, T instance) {
		if (instance instanceof MessageListener) {
			return (MessageListener) instance;
		}

		throw new NotSupportedException(clazz.getName());
	}

	default MessageListener getMessageListener(Class<?> clazz, Method method, MethodInvoker invoker) {
		return (message) -> {
			if (method.getParameterCount() == 1
					&& ClassUtils.isAssignableValue(method.getParameterTypes()[0], message)) {
				try {
					invoker.invoke(message);
				} catch (Throwable e) {
					ReflectionUtils.handleThrowable(e);
				}
			}
			throw new NotSupportedException(message.toString());
		};
	}
}
