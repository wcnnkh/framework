package scw.application;

import scw.beans.BeanFactory;
import scw.beans.Destroy;
import scw.beans.Init;
import scw.event.BasicEventDispatcher;
import scw.logger.Logger;
import scw.value.property.PropertyFactory;

public interface Application extends Init, Destroy, BasicEventDispatcher<ApplicationEvent> {
	void init();

	void destroy();

	BeanFactory getBeanFactory();

	PropertyFactory getPropertyFactory();

	/**
	 * 是否已经初始化了
	 * 
	 * @return
	 */
	boolean isInitialized();

	Logger getLogger();

	ClassLoader getClassLoader();
}
