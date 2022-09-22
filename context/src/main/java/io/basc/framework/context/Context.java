package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;

public interface Context extends Environment {

	ClassesLoader getSourceClasses();

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	/**
	 * 获取配置资源
	 * 
	 * @return
	 */
	Iterable<Resource> getConfigurationResources();

	ContextResolver getContextResolver();
}
