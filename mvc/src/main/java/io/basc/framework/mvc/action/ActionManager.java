package io.basc.framework.mvc.action;

import java.lang.reflect.Method;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.web.ServerHttpRequest;

public interface ActionManager extends BroadcastEventRegistry<ChangeEvent<Action>> {
	Action getAction(Method method);

	Action getAction(ServerHttpRequest request);

	Registration register(Action action);

	/**
	 * 监听新的action注册
	 */
	Registration registerListener(EventListener<ChangeEvent<Action>> eventListener);

	Elements<Action> getActions();
}