package io.basc.framework.jms.bind;

import javax.jms.MessageListener;

import io.basc.framework.reflect.MethodInvoker;

public interface MessageListenerFactory {
	MessageListener getMessageListener(MethodInvoker invoker);
}
