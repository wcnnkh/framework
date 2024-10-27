package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.config.BeanDefinition;

public interface BeanDefinitionReader {
	boolean canRead(Class<?> clazz);

	BeanDefinition read(Class<?> clazz);
}
