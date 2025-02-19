package io.basc.framework.mvc.action;

import java.lang.reflect.Method;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.util.actor.EventListener;
import io.basc.framework.util.actor.broadcast.BroadcastEventRegistry;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.event.ChangeEvent;
import io.basc.framework.util.register.Registration;

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