package scw.boot;

import scw.beans.BeanFactory;
import scw.context.ProviderClassesLoaderFactory;
import scw.context.Destroy;
import scw.context.Init;
import scw.env.Environment;
import scw.event.BasicEventDispatcher;
import scw.logger.Logger;

public interface Application extends ProviderClassesLoaderFactory, Init, Destroy, BasicEventDispatcher<ApplicationEvent> {
	BeanFactory getBeanFactory();

	Environment getEnvironment();
	
	/**
	 * 是否已经初始化了
	 * 
	 * @return
	 */
	boolean isInitialized();

	Logger getLogger();
	
	long getCreateTime();
}
