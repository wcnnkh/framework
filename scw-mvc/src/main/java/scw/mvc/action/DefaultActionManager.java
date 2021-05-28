package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.context.annotation.Provider;
import scw.event.ObjectEvent;
import scw.event.support.DefaultEventDispatcher;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.web.ServerHttpRequest;
import scw.web.pattern.HttpPattern;
import scw.web.pattern.HttpPatternRegistry;

@Provider
public class DefaultActionManager extends DefaultEventDispatcher<ObjectEvent<Action>> implements ActionManager {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private HttpPatternRegistry<Action> registry = new HttpPatternRegistry<Action>();
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
			registry.register(pattern, action);
		}
		publishEvent(new ObjectEvent<Action>(action));
	}

	@Override
	public Action getAction(Method method) {
		return actionMap.get(method);
	}
}
