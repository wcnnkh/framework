package scw.servlet.context;

import scw.core.Destroy;

public interface RequestBeanContext extends Destroy{
	<T> T getBean(Class<T> type);
	
	<T> T getBean(String name);
}
