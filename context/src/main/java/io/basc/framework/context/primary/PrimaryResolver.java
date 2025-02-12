package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.util.collections.Elements;

public interface PrimaryResolver {
	Elements<BeanFactoryPostProcessor> getBeanFactoryPostProcessors(Class<?> primaryClass);
}
