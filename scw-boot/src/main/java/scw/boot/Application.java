package scw.boot;

import scw.beans.BeanFactory;
import scw.context.Destroy;
import scw.context.Init;
import scw.context.ProviderClassesLoaderFactory;
import scw.env.Environment;
import scw.event.EventDispatcher;
import scw.logger.Logger;

public interface Application extends ProviderClassesLoaderFactory, Init, Destroy, EventDispatcher<ApplicationEvent> {
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
