package scw.jms.bind;

import javax.jms.MessageListener;

import scw.core.reflect.MethodInvoker;

public interface MessageListenerFactory {
	MessageListener getMessageListener(MethodInvoker invoker);
}
