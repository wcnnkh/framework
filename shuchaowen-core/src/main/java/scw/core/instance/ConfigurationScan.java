package scw.core.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import scw.core.instance.annotation.Configuration;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.comparator.CompareUtils;
import scw.util.value.property.PropertyFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigurationScan implements Comparator<Class<?>> {
	protected Logger logger = LoggerUtils.getConsoleLogger(getClass());

	public int compare(Class<?> o1, Class<?> o2) {
		Configuration c1 = o1.getAnnotation(Configuration.class);
		Configuration c2 = o2.getAnnotation(Configuration.class);
		return CompareUtils.compare(c1.order(), c2.order(), true);
	}

	protected Collection<Class<?>> scan(Class<?> type, Collection<String> packageNames) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> clazz : ResourceUtils.getPackageScan().getClasses(packageNames)) {
			if (clazz == type) {
				continue;
			}

			if (!ClassUtils.isAssignable(type, clazz)) {
				continue;
			}

			Configuration configuration = clazz.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			if (configuration.value().length != 0) {
				Collection<Class<?>> values = Arrays.asList(configuration.value());
				if (configuration.assignableValue()) {
					if (!ClassUtils.isAssignable(values, type)) {
						continue;
					}
				} else {
					if (!values.contains(type)) {
						continue;
					}
				}
			}

			if (!ClassUtils.isPresent(clazz.getName())) {
				logger.debug("not support class: {}", clazz.getName());
				continue;
			}

			list.add(clazz);
		}
		return list;
	}

	public <T> Collection<Class<T>> scan(Class<? extends T> type, PropertyFactory propertyFactory,
			Collection<? extends Class> excludeTypes, Collection<String> packageNames) {
		Set<Class<T>> set = new LinkedHashSet<Class<T>>();
		for (Class<?> clazz : scan(type, packageNames)) {
			Configuration configuration = clazz.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			if (ClassUtils.isAssignable(excludeTypes, clazz)) {
				continue;
			}

			set.add((Class<T>) clazz);
		}

		List<Class<T>> list = new ArrayList<Class<T>>(set);
		for (Class<? extends T> clazz : list) {
			Configuration c = clazz.getAnnotation(Configuration.class);
			for (Class<?> e : c.excludes()) {
				if (e == clazz) {
					continue;
				}
				set.remove(e);
			}
		}

		Collections.sort(list, this);

		set.clear();
		String[] configNames = propertyFactory.getObject(type.getName(), String[].class);
		if (!ArrayUtils.isEmpty(configNames)) {
			for (String name : configNames) {
				Class<?> clazz = ClassUtils.forNameNullable(name);
				if (clazz == null) {
					logger.warn("not create class by name: {}", name);
					continue;
				}

				if (ClassUtils.isAssignable(type, clazz)) {
					logger.warn("type [{}] not isAssignable class by name: {}", type, name);
					continue;
				}

				if (ClassUtils.isAssignable(excludeTypes, clazz)) {
					continue;
				}
				set.add((Class<T>) clazz);
			}
		}

		for (Class<T> clazz : list) {
			if (set.contains(clazz)) {
				continue;
			}

			if (!ReflectionUtils.isPresent(clazz)) {
				logger.debug("reflection not present [{}]", clazz);
				continue;
			}
			set.add(clazz);
		}
		return set;
	}
}
