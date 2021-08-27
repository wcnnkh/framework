package io.basc.framework.jms.bind;

import io.basc.framework.core.reflect.MethodInvoker;

import javax.jms.MessageListener;

public interface MessageListenerFactory {
	MessageListener getMessageListener(MethodInvoker invoker);
}
