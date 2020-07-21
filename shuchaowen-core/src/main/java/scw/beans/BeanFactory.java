package scw.beans;

import scw.aop.Aop;
import scw.beans.event.BeanEvent;
import scw.core.instance.InstanceFactory;
import scw.event.BasicEventDispatcher;

public interface BeanFactory extends InstanceFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);

	Aop getAop();

	/**
	 * bean的事件派发
	 * 
	 * @return
	 */
	BasicEventDispatcher<BeanEvent> getEventDispatcher();
}
