package scw.context.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.util.MultiIterator;

public class DefaultClassesLoader<S> implements ConfigurableClassesLoader<S>{
	private final List<ClassesLoader<S>> loaders = new LinkedList<ClassesLoader<S>>();
	private final Set<Class<S>> defaultClasses = new LinkedHashSet<Class<S>>();
	private final Accept<Class<S>> accept;
	
	public DefaultClassesLoader(){
		this(null);
	}
	
	public DefaultClassesLoader(@Nullable Accept<Class<S>> accept){
		this.accept = accept;
	}
	
	public List<ClassesLoader<S>> getLoaders() {
		return loaders;
	}
	
	public void add(Class<S> clazz) {
		if(accept != null && !accept.accept(clazz)){
			return ;
		}
		defaultClasses.add(clazz);
	}
	
	public void add(ClassesLoader<S> classesLoader) {
		loaders.add(accept == null? classesLoader:new AcceptClassesLoader<S>(classesLoader, accept, false));
	}

	public Set<Class<S>> getDefaultClasses() {
		return defaultClasses;
	}

	public void reload() {
		for(ClassesLoader<S> classesLoader : loaders){
			classesLoader.reload();
		}
	}

	public Iterator<Class<S>> iterator() {
		List<Iterator<Class<S>>> iterators = new ArrayList<Iterator<Class<S>>>(loaders.size() + 1);
		iterators.add(defaultClasses.iterator());
		for(ClassesLoader<S> classesLoader : loaders){
			iterators.add(classesLoader.iterator());
		}
		return new MultiIterator<Class<S>>(iterators);
	}

}
