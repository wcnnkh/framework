package scw.mvc.action;

import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.annotation.Provider;
import scw.event.EventListener;
import scw.mvc.annotation.Controller;

@Provider(order=Integer.MAX_VALUE)
public class ActionManagerPostProcesser implements ApplicationPostProcessor, EventListener<BeanLifeCycleEvent>{
	
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		application.getBeanFactory().registerListener(this);
	}

	public void onEvent(BeanLifeCycleEvent event) {
		Object source = event.getSource();
		if(source == null){
			return ;
		}
		
		if(event.getStep() != Step.AFTER_DEPENDENCE){
			return ;
		}
		
		if(source instanceof ActionManager){
			BeanFactory beanFactory = event.getBeanFactory();
			ActionManager actionManager = (ActionManager) source;
			//是否延迟加载实例
			boolean lazy = beanFactory.getEnvironment().getBooleanValue("action.bean.lazy.loading");
			for (Class<?> clz : beanFactory.getContextClassesLoader()) {
				if (!isSupport(beanFactory, clz)) {
					continue;
				}

				for (Method method : clz.getDeclaredMethods()) {
					if (!isSupport(method)) {
						continue;
					}

					Action action = new BeanAction(beanFactory, clz, method, lazy);
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
