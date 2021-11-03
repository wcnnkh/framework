package io.basc.framework.mvc.action;

import java.lang.reflect.Method;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.EventRegistry;
import io.basc.framework.event.ObjectEvent;
import io.basc.framework.web.ServerHttpRequest;

public interface ActionManager extends EventRegistry<ObjectEvent<Action>>, Iterable<Action> {
	Action getAction(Method method);

	Action getAction(ServerHttpRequest request);

	void register(Action action);

	/**
	 * 监听新的action注册
	 */
	EventRegistration registerListener(EventListener<ObjectEvent<Action>> eventListener);
}