package scw.context.support;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.util.Accept;

public class AcceptClassesLoader implements ClassesLoader{
	private final ClassesLoader classesLoader;
	private final Accept<Class<?>> accept;
	private final boolean cache;
	private volatile Set<Class<?>> cacheClasses;
	
	public AcceptClassesLoader(ClassesLoader classesLoader, Accept<Class<?>> accept, boolean cache){
		this.classesLoader = classesLoader;
		this.accept = accept;
		this.cache = cache;
	}
	
	private Set<Class<?>> filter(){
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for(Class<?> clazz : classesLoader){
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
	
	public Iterator<Class<?>> iterator() {
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
