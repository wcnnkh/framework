package scw.boot;

import scw.beans.ConfigurableBeanFactory;
import scw.context.ConfigurableClassesLoader;
import scw.env.ConfigurableEnvironment;

public interface ConfigurableApplication extends Application{
	ConfigurableBeanFactory getBeanFactory();
	
	ConfigurableEnvironment getEnvironment();
	
	ConfigurableClassesLoader<?> getContextClassesLoader();
}
