package scw.orm.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.orm.ColumnFactory;
import scw.orm.MethodColumn;

public class MethodColumnFactory implements ColumnFactory {
	private Collection<String> getterMethodPrefix;
	private Collection<String> setterMethodPrefix;

	public MethodColumnFactory(Collection<String> appendSetterMethodPrefix) {
		this.getterMethodPrefix = Arrays.asList("get", "is");
		LinkedList<String> setterNameList = new LinkedList<String>(appendSetterMethodPrefix);
		setterNameList.addFirst("set");
		this.setterMethodPrefix = setterNameList;
	}

	public MethodColumnFactory(Collection<String> getterMethodPrefix, Collection<String> setterMethodPrefix) {
		Assert.notEmpty(getterMethodPrefix);
		Assert.notEmpty(setterMethodPrefix);
		this.getterMethodPrefix = getterMethodPrefix;
		this.setterMethodPrefix = setterMethodPrefix;
	}

	public Map<String, MethodColumn> getColumnMap(Class<?> clazz) {
		Map<String, MethodColumn> map = new LinkedHashMap<String, MethodColumn>();
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			if (map.containsKey(field.getName())) {
				continue;
			}

			String methodNameSuffix = StringUtils.toLowerCase(field.getName(), 0, 1);
			map.put(field.getName(),
					createColumn(clazz, getMethod(clazz, getterMethodPrefix, methodNameSuffix, new Class<?>[0]),
							getMethod(clazz, setterMethodPrefix, methodNameSuffix, new Class<?>[0]), field,
							field.getName()));
		}

		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes == null || parameterTypes.length == 0) {
				for (String prefix : getterMethodPrefix) {
					if (StringUtils.isNotEmpty(prefix) && method.getName().startsWith(prefix)
							&& method.getName().length() > prefix.length()) {
						String name = method.getName().substring(prefix.length());
						String fieldName = StringUtils.toLowerCase(name, 0, 1);
						if (map.containsKey(fieldName)) {
							continue;
						}

						map.put(fieldName,
								createColumn(clazz, method,
										getMethod(clazz, setterMethodPrefix, name,
												new Class<?>[] { method.getReturnType() }),
										getField(clazz, fieldName), fieldName));
						break;
					}
				}
			}

			if (parameterTypes != null && parameterTypes.length == 1) {
				for (String prefix : setterMethodPrefix) {
					if (StringUtils.isNotEmpty(prefix) && method.getName().startsWith(prefix)
							&& method.getName().length() > prefix.length()) {
						String name = method.getName().substring(prefix.length());
						String fieldName = StringUtils.toLowerCase(name, 0, 1);
						if (map.containsKey(fieldName)) {
							continue;
						}

						map.put(fieldName,
								createColumn(clazz, getMethod(clazz, getterMethodPrefix, name, new Class<?>[0]), method,
										getField(clazz, fieldName), fieldName));
						break;
					}
				}
			}
		}
		return map;
	}

	protected MethodColumn createColumn(Class<?> clazz, Method getter, Method setter, Field field, String name) {
		return new DefaultMethodColumn(clazz, getter, setter, field, name);
	}

	private Method getMethod(Class<?> clazz, Collection<String> namePrefix, String startsUpperCaseName,
			Class<?>[] parameterTypes) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}

			for (String prefix : namePrefix) {
				if (method.getName().equals(prefix + startsUpperCaseName)) {
					if (Arrays.equals(parameterTypes, method.getParameterTypes())) {
						return method;
					}
				}
			}
		}
		return null;
	}

	private Field getField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
		}
		return field;
	}
}
