package scw.context.support;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.util.Accept;

public class AcceptClassesLoader<S> implements ClassesLoader<S>{
	private final ClassesLoader<S> classesLoader;
	private final Accept<Class<S>> accept;
	private final boolean cache;
	private volatile Set<Class<S>> cacheClasses;
	
	public AcceptClassesLoader(ClassesLoader<S> classesLoader, Accept<Class<S>> accept, boolean cache){
		this.classesLoader = classesLoader;
		this.accept = accept;
		this.cache = cache;
	}
	
	private Set<Class<S>> filter(){
		Set<Class<S>> classes = new LinkedHashSet<Class<S>>();
		for(Class<S> clazz : classesLoader){
			if(accept == null || accept.accept(clazz)){
				classes.add(clazz);
			}
		}
		return classes;
	}
	
	public void reload() {
		classesLoader.reload();
		if(cache){
			cacheClasses = filter();
		}
	}
	
	public Iterator<Class<S>> iterator() {
		if(cache){
			if(cacheClasses == null){
				synchronized (this) {
					if(cacheClasses == null){
						cacheClasses = filter();
					}
				}
			}
			return cacheClasses.iterator();
		}
		return filter().iterator();
	}
}
