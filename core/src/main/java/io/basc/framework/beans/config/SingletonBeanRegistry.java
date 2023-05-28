package io.basc.framework.beans.config;

import io.basc.framework.lang.Nullable;

public interface SingletonBeanRegistry {
	void registerSingleton(String name, Object singletonObject);

	@Nullable
	Object getSingleton(String name);

	boolean containsSingleton(String name);

	String[] getSingletonNames();
}