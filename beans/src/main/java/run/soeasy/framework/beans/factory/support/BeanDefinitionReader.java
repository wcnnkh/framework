package run.soeasy.framework.beans.factory.support;

import run.soeasy.framework.beans.factory.config.BeanDefinition;

public interface BeanDefinitionReader {
	boolean canRead(Class<?> clazz);

	BeanDefinition read(Class<?> clazz);
}
