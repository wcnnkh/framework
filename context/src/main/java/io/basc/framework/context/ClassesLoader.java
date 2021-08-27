package io.basc.framework.context;

import io.basc.framework.instance.ServiceLoader;

public interface ClassesLoader extends ServiceLoader<Class<?>> {
	static final String SUFFIX = ".class";
}
