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
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.ClassScanner;
import scw.util.comparator.CompareUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigurationScanner implements Comparator<Class<?>> {
	protected Logger logger = LoggerUtils.getLogger(getClass());

	public int compare(Class<?> o1, Class<?> o2) {
		Configuration c1 = o1.getAnnotation(Configuration.class);
		Configuration c2 = o2.getAnnotation(Configuration.class);
		return CompareUtils.compare(c1.order(), c2.order(), true);
	}

	protected Collection<Class<?>> scan(Class<?> type, Collection<String> packageNames) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> clazz : ClassScanner.getInstance().getClasses(packageNames)) {
			if (clazz == type) {//防止死循环
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

			if (!ReflectionUtils.isPresent(clazz)) {
				logger.debug("not support class: {}", clazz.getName());
				continue;
			}

			list.add(clazz);
		}
		return list;
	}

	public <T> Collection<Class<T>> scan(Class<? extends T> type,
			Collection<? extends Class> excludeTypes, Collection<String> packageNames) {
		Set<Class<T>> set = new LinkedHashSet<Class<T>>();
		for (Class<?> clazz : scan(type, packageNames)) {
			Configuration configuration = clazz.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			//排除
			if (ClassUtils.isAssignable(excludeTypes, clazz)) {
				continue;
			}

			set.add((Class<T>) clazz);
		}

		for (Class<? extends T> clazz : set) {
			Configuration c = clazz.getAnnotation(Configuration.class);
			for (Class<?> e : c.excludes()) {
				if (e == clazz) {
					continue;
				}
				set.remove(e);
			}
		}

		List<Class<T>> list = new ArrayList<Class<T>>(set);
		Collections.sort(list, this);
		return new LinkedHashSet<Class<T>>(list);
	}
}
