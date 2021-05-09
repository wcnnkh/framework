package scw.mvc.action;

import java.lang.reflect.Method;

import scw.event.EventRegistry;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.ObjectEvent;
import scw.http.server.ServerHttpRequest;

public interface ActionManager extends EventRegistry<ObjectEvent<Action>>, Iterable<Action> {
	Action getAction(Method method);

	Action getAction(ServerHttpRequest request);

	void register(Action action);

	/**
	 * 监听新的action注册
	 */
	EventRegistration registerListener(EventListener<ObjectEvent<Action>> eventListener);
}
