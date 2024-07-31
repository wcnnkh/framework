package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.util.register.LimitedRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatternMatcher;

@ConditionalOnParameters
public class DefaultActionManager extends StandardBroadcastEventDispatcher<ObservableEvent<Action>>
		implements ActionManager {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private HttpPatternMatcher<Action> registry = new HttpPatternMatcher<Action>();
	private Map<Method, Action> actionMap = new HashMap<Method, Action>();

	public Iterator<Action> iterator() {
		synchronized (actionMap) {
			return actionMap.values().iterator();
		}
	}

	public Action getAction(ServerHttpRequest request) {
		return registry.get(request);
	}

	public synchronized Registration register(Action action) {
		if (logger.isTraceEnabled()) {
			logger.trace("register action: {}", action);
		}

		Registration registration = Registration.EMPTY;
		synchronized (actionMap) {
			if (actionMap.containsKey(action.getMethod())) {
				throw new AlreadyExistsException(action.toString());
			}

			actionMap.put(action.getMethod(), action);
			registration = LimitedRegistration.of(() -> {
				synchronized (actionMap) {
					actionMap.remove(action.getMethod());
				}
			});
		}

		for (HttpPattern pattern : action.getPatternts()) {
			try {
				registration = registration.and(registry.add(pattern, action));
			} catch (Throwable e) {
				registration.unregister();
				throw e;
			}
		}

		publishEvent(new ObservableEvent<>(ChangeType.CREATE, action));
		return registration.and(() -> {
			publishEvent(new ObservableEvent<>(ChangeType.DELETE, action));
		});
	}

	@Override
	public Action getAction(Method method) {
		return actionMap.get(method);
	}
}
