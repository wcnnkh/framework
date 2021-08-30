package io.basc.framework.context;

import io.basc.framework.factory.ServiceLoader;

public interface ClassesLoader extends ServiceLoader<Class<?>> {
	static final String SUFFIX = ".class";
}
