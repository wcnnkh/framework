package scw.context.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.core.utils.ClassUtils;
import scw.util.comparator.CompareUtils;

public class ProviderClassesLoader<S> implements ClassesLoader<S>,
		Comparator<Class<S>> {
	private final ClassesLoader<?> classesLoader;
	private volatile Set<Class<S>> providers;
	private final Class<S> serviceClass;

	public ProviderClassesLoader(ClassesLoader<?> classesLoader,
			Class<S> serviceClass) {
		this.classesLoader = classesLoader;
		this.serviceClass = serviceClass;
	}

	public void reload() {
		classesLoader.reload();
		this.providers = getProivders();
	}
	
	@SuppressWarnings("unchecked")
	public Set<Class<S>> getProivders(){
		Set<Class<S>> list = new LinkedHashSet<Class<S>>();
		for (Class<?> clazz : classesLoader) {
			if (clazz.getName().equals(serviceClass.getName())) {// 防止死循环
				continue;
			}

			if (!ClassUtils.isAssignable(serviceClass, clazz)) {
				continue;
			}

			Provider provider = clazz.getAnnotation(Provider.class);
			if (provider == null) {
				continue;
			}

			if (provider.value().length != 0) {
				Collection<Class<?>> values = Arrays.asList(provider.value());
				if (provider.assignableValue()) {
					if (!ClassUtils.isAssignable(values, serviceClass)) {
						continue;
					}
				} else {
					if (!values.contains(serviceClass)) {
						continue;
					}
				}
			}

			list.add((Class<S>) clazz);
		}

		for (Class<S> clazz : list) {
			Provider provider = clazz.getAnnotation(Provider.class);
			for (Class<?> e : provider.excludes()) {
				if (e == clazz) {
					continue;
				}
				list.remove(e);
			}
		}
		
		if(list.isEmpty()){
			return Collections.emptySet();
		}

		List<Class<S>> classes = new ArrayList<Class<S>>(list);
		Collections.sort(classes, this);
		return new LinkedHashSet<Class<S>>(classes);
	}

	public int compare(Class<S> o1, Class<S> o2) {
		Provider c1 = o1.getAnnotation(Provider.class);
		Provider c2 = o2.getAnnotation(Provider.class);
		return CompareUtils.compare(c1.order(), c2.order(), true);
	}

	public Iterator<Class<S>> iterator() {
		if(providers == null){
			synchronized (this) {
				if(providers == null){
					this.providers = getProivders();
				}
			}
		}
		return providers.iterator();
	}

}
