package io.basc.framework.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class AccessibleFieldFunction implements Function<Class<?>, Stream<AccessibleField>> {
	private final ConcurrentReferenceHashMap<Class<?>, AccessibleField[]> cacheMap = new ConcurrentReferenceHashMap<>();
	private final String[] getterMethodPrefixs;
	private final String[] setterMethodPrefixs;

	public AccessibleFieldFunction() {
		this(new String[] { Getter.BOOLEAN_GETTER_METHOD_PREFIX, Getter.DEFAULT_GETTER_METHOD_PREFIX },
				new String[] { Setter.DEFAULT_SETTER_METHOD_PREFIX });
	}

	public AccessibleFieldFunction(String[] getterMethodPrefixs, String[] setterMethodPrefixs) {
		this.getterMethodPrefixs = getterMethodPrefixs == null ? new String[0] : getterMethodPrefixs.clone();
		this.setterMethodPrefixs = setterMethodPrefixs == null ? new String[0] : setterMethodPrefixs.clone();
	}

	protected Collection<Getter> getGetters(Class<?> currentClass, Field[] fields, Method[] methods) {
		LinkedHashSet<Getter> getters = new LinkedHashSet<Getter>();
		for (Field field : fields) {
			if (ReflectionUtils.isSerialVersionUIDField(field)) {
				continue;
			}

			Method getterMethod = ReflectionUtils.getDeclaredMethod(currentClass,
					MapperUtils.getGetterMethodName(field));
			if (getterMethod != null && Modifier.isStatic(getterMethod.getModifiers())
					&& !Modifier.isStatic(field.getModifiers())) {
				// 如果是静态方法但字段是非静态字段， 那么是不成立的
				getterMethod = null;
			}

			Getter getter = new DefaultGetter(currentClass, field.getName(), field, getterMethod);
			getters.add(getter);
		}

		for (Method method : methods) {
			if (ArrayUtils.isEmpty(method.getParameterTypes())) {
				for (String methodPrefix : getterMethodPrefixs) {
					if (method.getName().startsWith(methodPrefix)
							&& method.getName().length() > methodPrefix.length()) {
						String name = method.getName().substring(methodPrefix.length());
						Getter getter = new DefaultGetter(currentClass, StringUtils.toLowerCase(name, 0, 1), null,
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

	protected Collection<Setter> getSetters(Class<?> currentClass, Field[] fields, Method[] methods) {
		LinkedHashSet<Setter> setters = new LinkedHashSet<Setter>();
		for (Field field : fields) {
			if (ReflectionUtils.isSerialVersionUIDField(field)) {
				continue;
			}

			Method setterMethod = ReflectionUtils.getDeclaredMethod(currentClass,
					MapperUtils.getSetterMethodName(field), field.getType());
			if (setterMethod != null && Modifier.isStatic(setterMethod.getModifiers())
					&& !Modifier.isStatic(field.getModifiers())) {
				// 如果是静态方法但字段是非静态字段， 那么是不成立的
				setterMethod = null;
			}

			Setter setter = new DefaultSetter(currentClass, field.getName(), field, setterMethod);
			setters.add(setter);
		}

		for (Method method : methods) {
			if (method.getParameterTypes().length == 1) {
				for (String methodPrefix : setterMethodPrefixs) {
					if (method.getName().startsWith(methodPrefix)
							&& method.getName().length() > methodPrefix.length()) {
						String name = method.getName().substring(methodPrefix.length());
						Setter setter = new DefaultSetter(currentClass, StringUtils.toLowerCase(name, 0, 1), null,
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

	protected <T extends FieldDescriptor> T metadataFindAndRemove(FieldDescriptor fieldMetadata,
			Collection<T> metadatas) {
		Iterator<T> iterator = metadatas.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			if (t.getName().equals(fieldMetadata.getName()) && t.getType().equals(fieldMetadata.getType())) {
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

	protected List<AccessibleField> toFieldMetadatas(Collection<Getter> getters, Collection<Setter> setters) {
		List<AccessibleField> fields = new LinkedList<AccessibleField>();
		Iterator<Getter> getterIterator = getters.iterator();
		Iterator<Setter> setterIterator;
		while (getterIterator.hasNext()) {
			Getter getter = getterIterator.next();
			Setter setter = metadataFindAndRemove(getter, setters);
			AccessibleField fieldMetadata = new AccessibleField(getter, setter);
			fields.add(fieldMetadata);
		}

		setterIterator = setters.iterator();
		while (setterIterator.hasNext()) {
			Setter setter = setterIterator.next();
			Getter getter = metadataFindAndRemove(setter, getters);
			AccessibleField fieldMetadata = new AccessibleField(getter, setter);
			fields.add(fieldMetadata);
		}
		return fields;
	}

	public List<AccessibleField> getFieldMetadataList(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getDeclaredMethods();
		Collection<Getter> getters = getGetters(clazz, fields, methods);
		Collection<Setter> setters = getSetters(clazz, fields, methods);
		return toFieldMetadatas(getters, setters);
	}

	@Override
	public Stream<AccessibleField> apply(Class<?> sourceClass) {
		if (sourceClass == null || sourceClass == Object.class) {
			return StreamProcessorSupport.emptyStream();
		}

		AccessibleField[] metadatas = cacheMap.get(sourceClass);
		if (metadatas == null) {
			List<AccessibleField> list = getFieldMetadataList(sourceClass);
			if (CollectionUtils.isEmpty(list)) {
				return StreamProcessorSupport.emptyStream();
			}
			metadatas = list.toArray(new AccessibleField[0]);
			AccessibleField[] old = cacheMap.putIfAbsent(sourceClass, metadatas);
			if (old != null) {
				cacheMap.purgeUnreferencedEntries();
				metadatas = old;
			}
		}
		return Arrays.asList(metadatas).stream();
	}
}
