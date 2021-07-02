package scw.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanlifeCycleEvent;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.event.EventListener;
import scw.event.ObjectEvent;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpPatternResolvers;
import scw.mvc.security.HttpActionAuthorityManager;

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
		HttpPatternResolvers patternResolver = new HttpPatternResolvers(beanFactory);
		patternResolver.setPropertyResolver(beanFactory.getEnvironment());
		for (Class<?> clz : beanFactory.getContextClassesLoader()) {
			if (!patternResolver.canResolveHttpPattern(clz)) {
				continue;
			}
			
			for (Method method : clz.getDeclaredMethods()) {
				if (!patternResolver.canResolveHttpPattern(clz, method)) {
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
