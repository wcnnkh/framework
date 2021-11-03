package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.event.ObjectEvent;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatterns;

@Provider
public class DefaultActionManager extends SimpleEventDispatcher<ObjectEvent<Action>> implements ActionManager {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private HttpPatterns<Action> registry = new HttpPatterns<Action>();
	private Map<Method, Action> actionMap = new HashMap<Method, Action>();

	public DefaultActionManager() {
		super(true);
	}

	public Iterator<Action> iterator() {
		synchronized (actionMap) {
			return actionMap.values().iterator();
		}
	}

	public Action getAction(ServerHttpRequest request) {
		return registry.get(request);
	}

	public synchronized void register(Action action) {
		if (logger.isTraceEnabled()) {
			logger.trace("register action: {}", action);
		}

		synchronized (actionMap) {
			if (actionMap.containsKey(action.getMethod())) {
				throw new AlreadyExistsException(action.toString());
			}

			actionMap.put(action.getMethod(), action);
		}

		for (HttpPattern pattern : action.getPatternts()) {
			registry.add(pattern, action);
		}
		publishEvent(new ObjectEvent<Action>(action));
	}

	@Override
	public Action getAction(Method method) {
		return actionMap.get(method);
	}
}
