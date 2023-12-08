package io.basc.framework.mvc.context;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import io.basc.framework.beans.factory.BeanLifecycleEvent.Step;
import io.basc.framework.context.Context;
import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.ActionResolver;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionManager;
import io.basc.framework.mvc.action.BeanAction;
import io.basc.framework.mvc.security.HttpActionAuthorityManager;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.web.pattern.DefaultHttpPatternResolvers;
import io.basc.framework.web.pattern.HttpPatternResolvers;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class ActionContextPostProcesser implements ContextPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(ActionContextPostProcesser.class);

	@Override
	public void postProcessContext(ConfigurableContext context) {
		context.registerListener((event) -> {
			Object source = event.getBean();
			if (source == null) {
				return;
			}

			if (event.getStep() != Step.AFTER_INIT) {
				return;
			}

			ActionResolver actionResolver = context.getInstance(ActionResolver.class);
			if (source instanceof ActionManager) {
				actionManagerInit(context, (ActionManager) source, actionResolver);
			}
		});
	}

	private void actionManagerInit(Context context, ActionManager actionManager, ActionResolver actionResolver) {
		HttpPatternResolvers patternResolver = new DefaultHttpPatternResolvers();
		patternResolver.setPlaceholderFormat(context.getProperties());
		patternResolver.configure(context);
		for (Class<?> clz : context.getContextClasses()) {
			if (!patternResolver.canResolve(clz)) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				if (!EnableConditionUtils.enable(method, context.getProperties())) {
					continue;
				}

				if (!patternResolver.canResolve(clz, method)) {
					continue;
				}

				String controllerId = actionResolver.getControllerId(clz, method);
				if (!Modifier.isStatic(method.getModifiers())) {
					if (!context.isInstance(controllerId)) {
						logger.info("Unable to support this action {}", method);
						continue;
					}
				}

				Collection<String> filterNames = actionResolver.getActionInterceptorNames(clz, method);
				if (logger.isTraceEnabled()) {
					logger.trace("Register controller: {}", method);
				}

				Action action = new BeanAction(context, clz, method, patternResolver, controllerId, filterNames);
				actionManager.register(action);
			}
		}

		if (context.isInstance(HttpActionAuthorityManager.class)
				&& context.isSingleton(HttpActionAuthorityManager.class)) {
			HttpActionAuthorityManager actionAuthorityManager = context.getInstance(HttpActionAuthorityManager.class);
			for (Action action : actionManager) {
				actionAuthorityManager.register(action);
			}

			actionManager.registerListener((event) -> {
				if (event.getChangeType() == ChangeType.CREATE) {
					actionAuthorityManager.register(event.getSource());
				} else if (event.getChangeType() == ChangeType.DELETE) {
					actionAuthorityManager.unregister(event.getSource());
				}
			});
		}
	}
}
