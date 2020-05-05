package scw.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import scw.core.Assert;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.util.cache.CacheLoader;

public class DefaultFieldLoader implements
		CacheLoader<Class<?>, Collection<scw.core.reflect.Field>> {
	private final Collection<String> getterMethodPrefix;
	private final Collection<String> setterMethodPrefix;

	public DefaultFieldLoader(Collection<String> getterMethodPrefix,
			Collection<String> setterMethodPrefix) {
		Assert.notEmpty(getterMethodPrefix);
		Assert.notEmpty(setterMethodPrefix);
		this.getterMethodPrefix = getterMethodPrefix;
		this.setterMethodPrefix = setterMethodPrefix;
	}

	protected Collection<Getter> getGetters(Class<?> currentClass,
			Field[] fields, Method[] methods) {
		LinkedHashSet<Getter> getters = new LinkedHashSet<Getter>();
		for (Field field : fields) {
			Method getterMethod = ReflectionUtils
					.getMethod(
							currentClass,
							ReflectionUtils.getGetterMethodName(field,
									field.getName()));
			if (getterMethod != null
					&& Modifier.isStatic(getterMethod.getModifiers())
					&& !Modifier.isStatic(field.getModifiers())) {
				// 如果是静态方法但字段是非静态字段， 那么是不成立的
				getterMethod = null;
			}

			Getter getter = new DefaultGetter(currentClass, field.getName(),
					field, getterMethod);
			getters.add(getter);
		}

		for (Method method : methods) {
			if (ArrayUtils.isEmpty(method.getParameterTypes())) {
				for (String methodPrefix : getterMethodPrefix) {
					if (method.getName().startsWith(methodPrefix)
							&& method.getName().length() > methodPrefix
									.length()) {
						String name = method.getName().substring(
								methodPrefix.length());
						Getter getter = new DefaultGetter(currentClass,
								StringUtils.toLowerCase(name, 0, 1), null,
								method);
						if (getters.contains(getter)) {
							continue;
						}
						getters.add(getter);
					}
				}
			}
		}
		return getters;
	}

	protected Collection<Setter> getSetters(Class<?> currentClass,
			Field[] fields, Method[] methods) {
		LinkedHashSet<Setter> setters = new LinkedHashSet<Setter>();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Method setterMethod = ReflectionUtils
					.getMethod(
							currentClass,
							ReflectionUtils.getSetterMethodName(field,
									field.getName()));
			if (setterMethod != null
					&& Modifier.isStatic(setterMethod.getModifiers())
					&& !Modifier.isStatic(field.getModifiers())) {
				// 如果是静态方法但字段是非静态字段， 那么是不成立的
				setterMethod = null;
			}

			Setter setter = new DefaultSetter(currentClass, field.getName(),
					field, setterMethod);
			setters.add(setter);
		}

		for (Method method : methods) {
			if (method.getParameterTypes().length == 1) {
				for (String methodPrefix : setterMethodPrefix) {
					if (method.getName().startsWith(methodPrefix)
							&& method.getName().length() > methodPrefix
									.length()) {
						String name = method.getName().substring(
								methodPrefix.length());
						Setter setter = new DefaultSetter(currentClass,
								StringUtils.toLowerCase(name, 0, 1), null,
								method);
						if (setters.contains(setter)) {
							continue;
						}
						setters.add(setter);
					}
				}
			}
		}
		return setters;
	}

	protected <T extends FieldMetadata> T metadataFindAndRemove(
			FieldMetadata fieldMetadata, Collection<T> metadatas) {
		Iterator<T> iterator = metadatas.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			if (t.getName().equals(fieldMetadata.getName())
					&& t.getType() == fieldMetadata.getType()) {
				iterator.remove();
				return t;
			}
		}

		iterator = metadatas.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			if (t.getName().equals(fieldMetadata.getName())) {
				iterator.remove();
				return t;
			}
		}
		return null;
	}

	protected Collection<scw.core.reflect.Field> toFields(
			Collection<Getter> getters, Collection<Setter> setters) {
		List<scw.core.reflect.Field> fields = new LinkedList<scw.core.reflect.Field>();
		Iterator<Getter> getterIterator = getters.iterator();
		Iterator<Setter> setterIterator;
		while (getterIterator.hasNext()) {
			Getter getter = getterIterator.next();
			Setter setter = metadataFindAndRemove(getter, setters);
			scw.core.reflect.Field field = new scw.core.reflect.Field(getter,
					setter);
			fields.add(field);
		}

		setterIterator = setters.iterator();
		while (setterIterator.hasNext()) {
			Setter setter = setterIterator.next();
			Getter getter = metadataFindAndRemove(setter, getters);
			scw.core.reflect.Field field = new scw.core.reflect.Field(getter,
					setter);
			fields.add(field);
		}
		return fields;
	}

	public Collection<scw.core.reflect.Field> loader(Class<?> clazz)
			throws Exception {
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getDeclaredMethods();
		Collection<Getter> getters = getGetters(clazz, fields, methods);
		Collection<Setter> setters = getSetters(clazz, fields, methods);
		return toFields(getters, setters);
	}
}
