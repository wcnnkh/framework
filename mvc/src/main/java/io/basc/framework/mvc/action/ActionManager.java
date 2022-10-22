package io.basc.framework.mvc.action;

import java.lang.reflect.Method;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistry;
import io.basc.framework.event.ObjectEvent;
import io.basc.framework.util.Registration;
import io.basc.framework.web.ServerHttpRequest;

public interface ActionManager extends EventRegistry<ObjectEvent<Action>>, Iterable<Action> {
	Action getAction(Method method);

	Action getAction(ServerHttpRequest request);

	void register(Action action);

	/**
	 * 监听新的action注册
	 */
	Registration registerListener(EventListener<ObjectEvent<Action>> eventListener);
}