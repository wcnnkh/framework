package scw.context;

public interface ConfigurableClassesLoader<S> extends ClassesLoader<S>{
	void add(ClassesLoader<S> classesLoader);
	
	void add(Class<S> clazz);
}
