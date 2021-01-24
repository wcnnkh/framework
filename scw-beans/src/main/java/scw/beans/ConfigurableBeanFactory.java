package scw.beans;

import scw.context.ConfigurableClassesLoader;
import scw.env.ConfigurableEnvironment;

public interface ConfigurableBeanFactory extends BeanFactory,
		BeanDefinitionRegistry {
	ConfigurableClassesLoader<?> getContextClassesLoader();

	ConfigurableEnvironment getEnvironment();

	void registerSingletion(String name, Object instance);
}
