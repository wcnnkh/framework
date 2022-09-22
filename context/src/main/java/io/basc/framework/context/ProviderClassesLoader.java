package io.basc.framework.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;

public class ProviderClassesLoader implements ClassesLoader, Comparator<Class<?>> {
	private static Logger logger = LoggerFactory.getLogger(ProviderServiceLoader.class);
	private final ClassesLoader classesLoader;
	private volatile Set<Class<?>> providers;
	private final Class<?> serviceClass;
	private final ContextResolver contextResolver;

	public ProviderClassesLoader(ClassesLoader classesLoader, Class<?> serviceClass, ContextResolver contextResolver) {
		this.classesLoader = classesLoader;
		this.serviceClass = serviceClass;
		this.contextResolver = contextResolver;
	}

	public void reload() {
		classesLoader.reload();
		this.providers = getProivders();
	}

	public Set<Class<?>> getProivders() {
		Set<Class<?>> list = new LinkedHashSet<Class<?>>();
		for (Class<?> clazz : classesLoader) {
			if (clazz.getName().equals(serviceClass.getName())) {// 防止死循环
				continue;
			}

			if (!ClassUtils.isAssignable(serviceClass, clazz)) {
				continue;
			}

			ProviderDefinition providerDefinition = contextResolver.getProviderDefinition(clazz);
			if (providerDefinition == null) {
				continue;
			}

			Collection<Class<?>> providerNames = providerDefinition.getNames();
			if (providerNames.size() != 0) {
				if (providerDefinition.isAssignable()) {
					if (!isAssignable(providerNames)) {
						continue;
					}
				} else {
					if (!providerNames.contains(serviceClass)) {
						continue;
					}
				}
			}

			list.add(clazz);
		}

		for (Class<?> clazz : list) {
			Provider provider = clazz.getAnnotation(Provider.class);
			for (Class<?> e : provider.excludes()) {
				if (e == clazz) {
					continue;
				}
				list.remove(e);
			}
		}

		if (list.isEmpty()) {
			return Collections.emptySet();
		}

		List<Class<?>> classes = new ArrayList<Class<?>>(list);
		Collections.sort(classes, this);
		if (logger.isDebugEnabled()) {
			logger.debug("[{}] providers is {}", serviceClass, classes);
		}
		return new LinkedHashSet<Class<?>>(classes);
	}

	private boolean isAssignable(Class<?> clazz) {
		if (clazz == null || clazz == Object.class) {
			return false;
		}

		Class<?>[] interfaceClasses = clazz.getInterfaces();
		if (interfaceClasses != null) {
			for (Class<?> interfaceClass : interfaceClasses) {
				if (ClassUtils.isAssignable(serviceClass, interfaceClass)) {
					return true;
				}
			}
		}

		if (clazz == serviceClass) {
			return true;
		}

		return isAssignable(clazz.getSuperclass());
	}

	public boolean isAssignable(Collection<Class<?>> services) {
		for (Class<?> clazz : services) {
			if (isAssignable(clazz)) {
				return true;
			}
		}
		return false;
	}

	public int compare(Class<?> o1, Class<?> o2) {
		ProviderDefinition c1 = contextResolver.getProviderDefinition(o1);
		ProviderDefinition c2 = contextResolver.getProviderDefinition(o2);
		return OrderComparator.INSTANCE.compare(c1.getOrder(), c2.getOrder());
	}

	public Iterator<Class<?>> iterator() {
		if (providers == null) {
			synchronized (this) {
				if (providers == null) {
					this.providers = getProivders();
				}
			}
		}
		return providers.iterator();
	}

}
