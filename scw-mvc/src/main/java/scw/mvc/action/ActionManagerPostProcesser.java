package scw.mvc.action;

import java.lang.reflect.Method;

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
import scw.mvc.annotation.Controller;
import scw.mvc.security.HttpActionAuthorityManager;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class ActionManagerPostProcesser implements BeanFactoryPostProcessor, EventListener<BeanlifeCycleEvent> {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener(this);

		if (beanFactory.isInstance(HttpActionAuthorityManager.class)
				&& beanFactory.isSingleton(HttpActionAuthorityManager.class)
				&& beanFactory.isInstance(ActionManager.class) && beanFactory.isSingleton(ActionManager.class)) {
			ActionManager actionManager = beanFactory.getInstance(ActionManager.class);
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

	public void onEvent(BeanlifeCycleEvent event) {
		Object source = event.getSource();
		if (source == null) {
			return;
		}

		if (event.getStep() != Step.AFTER_DEPENDENCE) {
			return;
		}

		if (source instanceof ActionManager) {
			BeanFactory beanFactory = event.getBeanFactory();
			ActionManager actionManager = (ActionManager) source;
			for (Class<?> clz : beanFactory.getContextClassesLoader()) {
				if (!isSupport(beanFactory, clz)) {
					continue;
				}

				for (Method method : clz.getDeclaredMethods()) {
					if (!isSupport(method)) {
						continue;
					}

					Action action = new BeanAction(beanFactory, clz, method);
					actionManager.register(action);
				}
			}
		}
	}

	protected boolean isSupport(BeanFactory beanFactory, Class<?> clazz) {
		Controller clzController = clazz.getAnnotation(Controller.class);
		if (clzController == null) {
			return false;
		}

		return beanFactory.isInstance(clazz);
	}

	protected boolean isSupport(Method method) {
		return method.getAnnotation(Controller.class) != null;
	}
}
