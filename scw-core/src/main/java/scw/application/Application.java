package scw.application;

import scw.beans.BeanFactory;
import scw.beans.Destroy;
import scw.beans.Init;
import scw.event.BasicEventDispatcher;
import scw.value.property.PropertyFactory;

public interface Application extends Init, Destroy, BasicEventDispatcher<ApplicationEvent>{
	void init();
	
	boolean isStarted();

	void destroy();
	
	BeanFactory getBeanFactory();

	PropertyFactory getPropertyFactory();
}
