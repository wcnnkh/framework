package scw.boot;

import scw.beans.ConfigurableBeanFactory;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContextEnvironment;

public interface ConfigurableApplication extends Application{
	ConfigurableBeanFactory getBeanFactory();
	
	ConfigurableContextEnvironment getEnvironment();
	
	ConfigurableClassesLoader<?> getContextClassesLoader();
}
