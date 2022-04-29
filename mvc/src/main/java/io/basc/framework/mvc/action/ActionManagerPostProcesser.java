package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeanlifeCycleEvent;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.ObjectEvent;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.security.HttpActionAuthorityManager;
import io.basc.framework.web.pattern.DefaultHttpPatternResolvers;
import io.basc.framework.web.pattern.HttpPatternResolvers;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class ActionManagerPostProcesser implements BeanFactoryPostProcessor, EventListener<BeanlifeCycleEvent> {
	private static Logger logger = LoggerFactory.getLogger(ActionManagerPostProcesser.class);

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener(this);
	}

	public void onEvent(BeanlifeCycleEvent event) {
		Object source = event.getSource();
		if (source == null) {
			return;
		}

		if (event.getStep() != Step.AFTER_INIT) {
			return;
		}

		if (source instanceof ActionManager) {
			actionManagerInit(event.getBeanFactory(), (ActionManager) source);
		}
	}

	private void actionManagerInit(BeanFactory beanFactory, ActionManager actionManager) {
		HttpPatternResolvers patternResolver = new DefaultHttpPatternResolvers();
		patternResolver.setPlaceholderFormat(beanFactory.getEnvironment());
		patternResolver.configure(beanFactory);
		for (Class<?> clz : beanFactory.getContextClasses()) {
			if (!patternResolver.canResolve(clz)) {
				continue;
			}
			
			for (Method method : clz.getDeclaredMethods()) {
				if(!EnableConditionUtils.enable(method, beanFactory.getEnvironment())) {
					continue;
				}
				
				if (!patternResolver.canResolve(clz, method)) {
					continue;
				}
				
				// 如果是非静态方法，说明要使用beanFactory进行实体化，此时应该判断是否可以实例化
				if (!Modifier.isStatic(method.getModifiers()) && !beanFactory.isInstance(clz)) {
					if(logger.isDebugEnabled()) {
						logger.debug("Unsupported controller: {}", method);
					}
					continue;
				}

				if(logger.isTraceEnabled()) {
					logger.trace("Register controller: {}", method);
				}
				Action action = new BeanAction(beanFactory, clz, method, patternResolver);
				actionManager.register(action);
			}
		}

		if (beanFactory.isInstance(HttpActionAuthorityManager.class)
				&& beanFactory.isSingleton(HttpActionAuthorityManager.class)) {
			HttpActionAuthorityManager actionAuthorityManager = beanFactory
					.getInstance(HttpActionAuthorityManager.class);
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
