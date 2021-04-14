package scw.context;

import scw.instance.ServiceLoader;

public interface ClassesLoader extends ServiceLoader<Class<?>> {
	static final String SUFFIX = ".class";
}
