package io.basc.framework.mvc.action;

import java.lang.reflect.Method;

import io.basc.framework.util.Elements;
import io.basc.framework.util.actor.EventListener;
import io.basc.framework.util.actor.broadcast.BroadcastEventRegistry;
import io.basc.framework.util.exchange.event.ChangeEvent;
import io.basc.framework.util.register.Registration;
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