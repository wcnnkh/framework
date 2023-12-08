package io.basc.framework.mvc.security;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.mvc.ActionResolver;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.DefaultHttpAuthorityManager;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.web.pattern.HttpPattern;

@ConditionalOnParameters(value = HttpActionAuthorityManager.class)
public class DefaultHttpActionAuthorityManager extends DefaultHttpAuthorityManager<HttpAuthority>
		implements HttpActionAuthorityManager {
	private Map<Action, Registration> actionMap = new HashMap<>();
	private final ActionResolver actionResolver;

	public DefaultHttpActionAuthorityManager(ActionResolver actionResolver) {
		Assert.requiredArgument(actionResolver != null, "actionResolver");
		this.actionResolver = actionResolver;
	}

	public ActionResolver getActionResolver() {
		return actionResolver;
	}

	public Registration register(Action action) {
		Assert.requiredArgument(action != null, "action");

		synchronized (this) {
			if (actionMap.containsKey(action)) {
				throw new AlreadyExistsException(action.toString());
			}

			Registration registration = actionResolver.registerHttpAuthority(this, action);
			actionMap.put(action, registration);
			return registration;
		}
	}

	@Override
	public void unregister(Action action) {
		Registration registration = actionMap.remove(action);
		if (registration == null) {
			synchronized (this) {
				if (registration == null) {
					registration = actionMap.remove(action);
				}
			}
		}

		if (registration != null) {
			registration.unregister();
		}
	}

	public HttpAuthority getAuthority(Action action) {
		for (HttpPattern descriptor : action.getPatternts()) {
			HttpAuthority authority = getAuthority(descriptor.getPath(), descriptor.getMethod());
			if (authority != null) {
				return authority;
			}
		}
		return null;
	}
}
