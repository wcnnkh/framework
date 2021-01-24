package scw.context;

import java.util.Iterator;

import scw.instance.ServiceLoader;


public interface ClassesLoader<S> extends ServiceLoader<Class<S>> {
	static final String SUFFIX = ".class";
	
	Iterator<Class<S>> iterator();
}
