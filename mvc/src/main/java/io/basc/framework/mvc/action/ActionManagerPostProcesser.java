package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.ObjectEvent;
import io.basc.framework.factory.BeanlifeCycleEvent.Step;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.security.HttpActionAuthorityManager;
import io.basc.framework.web.pattern.DefaultHttpPatternResolvers;
import io.basc.framework.web.pattern.HttpPatternResolvers;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class ActionManagerPostProcesser implements ContextPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(ActionManagerPostProcesser.class);

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

			if (source instanceof ActionManager) {
				actionManagerInit(context, (ActionManager) source);
			}
		});
	}

	private void actionManagerInit(Context context, ActionManager actionManager) {
		HttpPatternResolvers patternResolver = new DefaultHttpPatternResolvers();
		patternResolver.setPlaceholderFormat(context);
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

				// 如果是非静态方法，说明要使用beanFactory进行实体化，此时应该判断是否可以实例化
				if (!Modifier.isStatic(method.getModifiers()) && !context.isInstance(clz)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Unsupported controller: {}", method);
					}
					continue;
				}

				if (logger.isTraceEnabled()) {
					logger.trace("Register controller: {}", method);
				}
				Action action = new BeanAction(context, clz, method, patternResolver);
				actionManager.register(action);
			}
		}

		if (context.isInstance(HttpActionAuthorityManager.class)
				&& context.isSingleton(HttpActionAuthorityManager.class)) {
			HttpActionAuthorityManager actionAuthorityManager = context.getInstance(HttpActionAuthorityManager.class);
			for (Action action : actionManager) {
				actionAuthorityManager.register(action);
			}

			actionManager.registerListener(new EventListener<ObjectEvent<Action>>() {

				@Override
				public void onEvent(ObjectEvent<Action> event) {
					actionAuthorityManager.register(event.getSource());
				}
			});
		}
	}
}
