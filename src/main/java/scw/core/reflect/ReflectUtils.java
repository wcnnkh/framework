package scw.core.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.Assert;
import scw.core.PropertyFactory;
import scw.core.Verification;
import scw.core.annotation.Order;
import scw.core.exception.AlreadyExistsException;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.CompareUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.logger.LoggerUtils;

public final class ReflectUtils {
	private ReflectUtils() {
	};

	public static void loadMethod(Object bean, String propertyPrefix, PropertyFactory propertyFactory,
			final InstanceFactory instanceFactory, Set<String> ignoreNames) {
		loadMethod(bean, Arrays.asList("set", "add"), propertyPrefix, propertyFactory, instanceFactory, ignoreNames,
				null);
	}

	public static void loadMethod(Object bean, Collection<String> methodPrefixs, String propertyPrefix,
			PropertyFactory propertyFactory, final InstanceFactory instanceFactory, final Set<String> ignoreName,
			final Verification<Type> beanVerification) {
		loadMethod(bean, methodPrefixs, propertyPrefix, propertyFactory, new PropertyMapper<String>() {

			public Object mapper(String name, String value, Type type) throws Exception {
				if (StringUtils.isEmpty(value)) {
					return null;
				}

				if (ignoreName != null && ignoreName.contains(name)) {
					return null;
				}

				if (StringParse.isCommonType(type)) {
					return StringParse.defaultParse(value, type);
				}

				if (TypeUtils.isInterface(type) || TypeUtils.isAbstract(type)) {
					String className = TypeUtils.getClassName(type);
					return instanceFactory.isInstance(className) ? instanceFactory.getInstance(className) : null;
				}

				if (beanVerification == null) {
					return StringParse.defaultParse(value, type);
				}

				if (beanVerification.verification(type)) {
					String className = TypeUtils.getClassName(type);
					return instanceFactory.isInstance(className) ? instanceFactory.getInstance(className) : null;
				} else {
					return StringParse.defaultParse(value, type);
				}
			}
		});
	}

	public static void loadMethod(Object bean, Collection<String> methodPrefixs, String propertyPrefix,
			PropertyFactory propertyFactory, PropertyMapper<String> propertyMapper) {
		if (CollectionUtils.isEmpty(methodPrefixs)) {
			return;
		}

		for (Method method : bean.getClass().getDeclaredMethods()) {
			Type[] types = method.getGenericParameterTypes();
			if (types.length != 1) {
				continue;
			}

			for (String methodPrefix : methodPrefixs) {
				if (method.getName().startsWith(methodPrefix)) {
					String name = method.getName().substring(methodPrefix.length());
					name = StringUtils.toLowerCase(name, 0, 1);
					String key = StringUtils.isEmpty(propertyPrefix) ? name : (propertyPrefix + name);
					String value = propertyFactory.getProperty(key);
					Object v;
					try {
						v = propertyMapper.mapper(name, value, types[0]);
						if (v != null) {
							ReflectUtils.setAccessibleMethod(method);
							method.invoke(bean, v);
						}
					} catch (Exception e) {
						LoggerUtils.warn(ReflectUtils.class, "向对象{}，插入name={},value={}时异常", bean.getClass().getName(),
								name, value);
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static <T, V> void setProperties(Class<T> type, T bean, Map<String, V> properties,
			PropertyMapper<V> mapper) {
		if (properties == null || properties.isEmpty()) {
			return;
		}

		for (Entry<String, V> entry : properties.entrySet()) {
			Method[] methods = findSetterMethods(type, entry.getKey(), true);
			if (methods == null) {
				Field field = getField(type, entry.getKey(), true);
				if (field == null) {
					continue;
				}

				Object value;
				try {
					value = mapper.mapper(entry.getKey(), entry.getValue(), type);
					field.set(bean, value);
				} catch (Exception e) {
					LoggerUtils.warn(ReflectUtils.class, "向对象{}，插入name={},value={}时异常", type.getName(), entry.getKey(),
							entry.getValue());
					e.printStackTrace();
				}
				continue;
			}

			for (Method method : methods) {
				Object value;
				try {
					value = mapper.mapper(entry.getKey(), entry.getValue(), method.getParameterTypes()[0]);
					method.invoke(bean, value);
				} catch (Exception e) {
					LoggerUtils.warn(ReflectUtils.class, "向对象{}，插入name={},value={}时异常(调用set方法)", type.getName(),
							entry.getKey(), entry.getValue());
					e.printStackTrace();
				}
			}
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, boolean isPublic) {
		Constructor<T> constructor = null;
		if (isPublic) {
			try {
				constructor = type.getConstructor();
			} catch (NoSuchMethodException e) {
			}
		} else {
			try {
				constructor = type.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
			}

			if (constructor != null && !Modifier.isPublic(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
		}
		return constructor;
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, boolean isPublic, Class<?>... parameterTypes) {
		Constructor<T> constructor;
		if (isPublic) {
			try {
				constructor = type.getConstructor(parameterTypes);
			} catch (NoSuchMethodException e) {
				return null;
			}
		} else {
			try {
				constructor = type.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException e) {
				return null;
			}

			if (!Modifier.isPublic(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
		}
		return constructor;
	}

	public static Constructor<?> getConstructor(String className, boolean isPublic, Class<?>... parameterTypes)
			throws ClassNotFoundException {
		return getConstructor(Class.forName(className), isPublic, parameterTypes);
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, boolean isPublic, String... parameterTypeNames)
			throws ClassNotFoundException {
		return getConstructor(type, isPublic, ClassUtils.forName(parameterTypeNames));
	}

	public static Constructor<?> getConstructor(String className, boolean isPublic, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException {
		return getConstructor(Class.forName(className), isPublic, ClassUtils.forName(className));
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> findConstructor(Class<T> type, boolean isPublic, Class<?>... parameterTypes) {
		for (Constructor<?> constructor : isPublic ? type.getConstructors() : type.getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == parameterTypes.length) {
				boolean find = true;
				for (int i = 0; i < types.length; i++) {
					if (!ClassUtils.isAssignable(parameterTypes[i], types[i])) {
						find = false;
						break;
					}
				}

				if (find) {
					if (!isPublic && !Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					return (Constructor<T>) constructor;
				}
			}
		}
		return null;
	}

	/**
	 * 此方法可能返回空
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> findConstructorByParameters(Class<T> type, boolean isPublic, Object... params) {
		for (Constructor<?> constructor : isPublic ? type.getConstructors() : type.getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == params.length) {
				boolean find = true;
				for (int i = 0; i < types.length; i++) {
					Object v = params[i];
					if (v == null) {
						continue;
					}

					if (!ClassUtils.isAssignableValue(types[i], v)) {
						find = false;
						break;
					}
				}

				if (find) {
					if (!isPublic && !Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					return (Constructor<T>) constructor;
				}
			}
		}
		return null;
	}

	/**
	 * 根据参数名来调用方法
	 * 
	 * @param type
	 * @param instance
	 * @param name
	 * @param isPublic
	 * @param parameterMap
	 * @return
	 */
	public static <T> Object invoke(Class<T> type, Object instance, String name, boolean isPublic,
			Map<String, Object> parameterMap) throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return getMethod(type, isPublic, name).invoke(instance);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		for (Method method : isPublic ? type.getMethods() : type.getDeclaredMethods()) {
			if (size == method.getParameterTypes().length) {
				String[] names = ParameterUtils.getParameterName(method);
				Object[] args = new Object[size];
				boolean find = true;
				for (int i = 0; i < names.length; i++) {
					if (!parameterMap.containsKey(names[i])) {
						find = false;
						break;
					}

					args[i] = parameterMap.get(names[i]);
				}

				if (find) {
					if (!Modifier.isPublic(method.getModifiers())) {
						method.setAccessible(true);
					}
					try {
						return method.invoke(instance, args);
					} catch (Exception e) {
						new RuntimeException(e);
					}
					break;
				}
			}
		}

		throw new NoSuchMethodException(type.getName() + ", method=" + name);
	}

	public static Method getMethod(Class<?> clazz, boolean isPublic, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Method method;
		if (isPublic) {
			method = clazz.getMethod(name, parameterTypes);
		} else {
			method = clazz.getDeclaredMethod(name, parameterTypes);

			if (!Modifier.isPublic(clazz.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
		}
		return method;
	}

	public static Method findMethod(String className, String methodName, Class<?>... parameterTypes) {
		Class<?> clz = null;
		try {
			clz = Class.forName(className);
		} catch (Throwable e) {
		}

		if (clz == null) {
			return null;
		}

		return findMethod(clz, methodName, parameterTypes);
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		Method method;
		try {
			method = clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}

		if (!Modifier.isPublic(clazz.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
		}
		return method;
	}

	public static int getMethodCountForName(Class<?> clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		int count = 0;
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (methodName.equals(method.getName())) {
				count++;
			}
		}
		Class<?>[] ifcs = clazz.getInterfaces();
		for (Class<?> ifc : ifcs) {
			count += getMethodCountForName(ifc, methodName);
		}
		if (clazz.getSuperclass() != null) {
			count += getMethodCountForName(clazz.getSuperclass(), methodName);
		}
		return count;
	}

	/**
	 * Does the given class or one of its superclasses at least have one or more
	 * methods with the supplied name (with any argument types)? Includes
	 * non-public methods.
	 * 
	 * @param clazz
	 *            the clazz to check
	 * @param methodName
	 *            the name of the method
	 * @return whether there is at least one method with the given name
	 */
	public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		Class<?>[] ifcs = clazz.getInterfaces();
		for (Class<?> ifc : ifcs) {
			if (hasAtLeastOneMethodWithName(ifc, methodName)) {
				return true;
			}
		}
		return (clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName));
	}

	/**
	 * Given a method, which may come from an interface, and a target class used
	 * in the current reflective invocation, find the corresponding target
	 * method if there is one. E.g. the method may be {@code IFoo.bar()} and the
	 * target class may be {@code DefaultFoo}. In this case, the method may be
	 * {@code DefaultFoo.bar()}. This enables attributes on that method to be
	 * found.
	 * <p>
	 * <b>NOTE:</b> In contrast to
	 * {@link shuchaowen.spring.aop.support.AopUtils#getMostSpecificMethod},
	 * this method does <i>not</i> resolve Java 5 bridge methods automatically.
	 * Call
	 * {@link shuchaowen.spring.core.reference.spring.core.spring.core.BridgeMethodResolver#findBridgedMethod}
	 * if bridge method resolution is desirable (e.g. for obtaining metadata
	 * from the original method definition).
	 * <p>
	 * <b>NOTE:</b> Since Spring 3.1.1, if Java security settings disallow
	 * reflective access (e.g. calls to {@code Class#getDeclaredMethods} etc,
	 * this implementation will fall back to returning the originally provided
	 * method.
	 * 
	 * @param method
	 *            the method to be invoked, which may come from an interface
	 * @param targetClass
	 *            the target class for the current invocation. May be
	 *            {@code null} or may not even implement the method.
	 * @return the specific target method, or the original method if the
	 *         {@code targetClass} doesn't implement it or is {@code null}
	 */
	public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
		if (method != null && isOverridable(method, targetClass) && targetClass != null
				&& !targetClass.equals(method.getDeclaringClass())) {
			try {
				if (Modifier.isPublic(method.getModifiers())) {
					try {
						return targetClass.getMethod(method.getName(), method.getParameterTypes());
					} catch (NoSuchMethodException ex) {
						return method;
					}
				} else {
					Method specificMethod = findMethod(targetClass, method.getName(), method.getParameterTypes());
					return (specificMethod != null ? specificMethod : method);
				}
			} catch (AccessControlException ex) {
				// Security settings are disallowing reflective access; fall
				// back to 'method' below.
			}
		}
		return method;
	}

	/**
	 * Determine whether the given method is overridable in the given target
	 * class.
	 * 
	 * @param method
	 *            the method to check
	 * @param targetClass
	 *            the target class to check against
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isOverridable(Method method, Class targetClass) {
		if (Modifier.isPrivate(method.getModifiers())) {
			return false;
		}
		if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
			return true;
		}
		return ClassUtils.getPackageName(method.getDeclaringClass()).equals(ClassUtils.getPackageName(targetClass));
	}

	/**
	 * Return the qualified name of the given method, consisting of fully
	 * qualified interface/class name + "." + method name.
	 * 
	 * @param method
	 *            the method
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method) {
		Assert.notNull(method, "Method must not be null");
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static Field getField(Class<?> type, String name, boolean sup) {
		Class<?> clz = type;
		Field field;
		while (clz != null && clz != Object.class) {
			try {
				field = clz.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
		return null;
	}

	public static Method findGetterMethod(Class<?> clazz, String fieldName, boolean sup) {
		Method find = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Method method : clz.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 0) {
					continue;
				}

				if (TypeUtils.isBoolean(method.getReturnType())) {
					String methodNameSuffix = fieldName;
					if (fieldName.startsWith("is")) {
						methodNameSuffix = fieldName.substring(2);
					}

					if (method.getName().equals("is" + StringUtils.toUpperCase(methodNameSuffix, 0, 1))) {
						find = method;
					} else if (method.getName().equals("is" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}

					if (find != null && fieldName.startsWith("is")) {
						LoggerUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
								fieldName);
					}
				} else {
					if (method.getName().equals("get" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}
				}

				if (find != null) {
					find.setAccessible(true);
					break;
				}
			}

			if (find != null) {
				break;
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
		return find;
	}

	public static Method[] findSetterMethods(Class<?> clazz, String fieldName, boolean sup) {
		LinkedList<Method> methods = new LinkedList<Method>();
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Method method : clz.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}

				Method find = null;
				if (TypeUtils.isBoolean(method.getParameterTypes()[0])) {
					String methodNameSuffix = fieldName;
					if (fieldName.startsWith("is")) {
						methodNameSuffix = fieldName.substring(2);
					}

					if (method.getName().equals("set" + StringUtils.toUpperCase(methodNameSuffix, 0, 1))) {
						find = method;
					} else if (method.getName().equals("set" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}

					if (find != null && fieldName.startsWith("is")) {
						LoggerUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
								fieldName);
					}
				} else {
					if (method.getName().equals("set" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}
				}

				if (find != null) {
					find.setAccessible(true);
					methods.add(find);
				}
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
		return methods.isEmpty() ? null : methods.toArray(new Method[methods.size()]);
	}

	public static Method findSetterMethod(Class<?> clz, String fieldName, boolean sup) {
		Method[] methods = findSetterMethods(clz, fieldName, sup);
		return methods == null ? null : methods[0];
	}

	public static Method getGetterMethod(Class<?> clazz, Field field, boolean sup) {
		Method getter = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			if (TypeUtils.isBoolean(field.getType())) {
				String methodNameSuffix = field.getName();
				if (methodNameSuffix.startsWith("is")) {
					LoggerUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
							methodNameSuffix);
					methodNameSuffix = methodNameSuffix.substring(2);
				}
				try {
					getter = clz.getDeclaredMethod("is" + StringUtils.toUpperCase(methodNameSuffix, 0, 1));
				} catch (NoSuchMethodException e1) {
					try {
						getter = clz.getDeclaredMethod("is" + StringUtils.toUpperCase(field.getName(), 0, 1));
					} catch (NoSuchMethodException e) {
					}
				}
			} else {
				try {
					getter = clz.getDeclaredMethod("get" + StringUtils.toUpperCase(field.getName(), 0, 1));
				} catch (NoSuchMethodException e) {
				}
			}

			if (getter != null) {
				getter.setAccessible(true);
				break;
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
		return getter;
	}

	public static Method getSetterMethod(Class<?> clazz, Field field, boolean sup) {
		Method setter = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			if (TypeUtils.isBoolean(field.getType())) {
				String methodNameSuffix = field.getName();
				if (methodNameSuffix.startsWith("is")) {
					LoggerUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
							methodNameSuffix);
					methodNameSuffix = methodNameSuffix.substring(2);
				}

				try {
					setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(methodNameSuffix, 0, 1),
							field.getType());
				} catch (NoSuchMethodException e1) {
					try {
						setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(field.getName(), 0, 1),
								field.getType());
					} catch (NoSuchMethodException e) {
					}
				}
			} else {
				try {
					setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(field.getName(), 0, 1),
							field.getType());
				} catch (NoSuchMethodException e) {
				}
			}

			if (setter != null) {
				setter.setAccessible(true);
				break;
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
		return setter;
	}

	/**
	 * 提取多个对象的字段
	 * 
	 * @param objList
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getFieldValueList(Collection<?> objList, String fieldName) {
		if (CollectionUtils.isEmpty(objList) || StringUtils.isEmpty(fieldName)) {
			return Collections.EMPTY_LIST;
		}

		List<T> list = new ArrayList<T>(objList.size());
		Iterator<?> iterator = objList.iterator();
		Field field = null;
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			if (field == null) {
				field = getField(obj.getClass(), fieldName, true);
				if (field == null) {
					throw new NotFoundException(fieldName);
				}
			}

			Object v;
			try {
				v = field.get(obj);
				if (v == null) {
					continue;
				}

				list.add((T) v);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> listToMap(String keyFieldName, Iterable<V> list) {
		if (list == null) {
			return Collections.EMPTY_MAP;
		}

		Field field = null;
		Map<K, V> map = null;
		for (V v : list) {
			if (v == null) {
				continue;
			}

			if (map == null) {
				map = new HashMap<K, V>();
			}

			if (field == null) {
				field = getField(v.getClass(), keyFieldName, true);
				if (field == null) {
					throw new NotFoundException("list转map时无法在实体中找到此字段：" + keyFieldName);
				}
			}

			K fv = null;
			try {
				fv = (K) field.get(v);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if (fv == null) {
				throw new NullPointerException("key不能为空[" + keyFieldName + "]");
			}

			if (map.containsKey(fv)) {
				throw new AlreadyExistsException("list转map时发现已经存在相同的key[" + fv + "], fieldName=" + keyFieldName);
			}

			map.put(fv, v);
		}

		if (map == null) {
			return Collections.EMPTY_MAP;
		}
		return map;
	}

	public static void setAccessibleField(Field field) {
		if (!field.isAccessible()
				&& (Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers()))) {
			field.setAccessible(true);
		}
	}

	public static void setAccessibleMethod(Method method) {
		if (!method.isAccessible()
				&& (Modifier.isPrivate(method.getModifiers()) || Modifier.isProtected(method.getModifiers()))) {
			method.setAccessible(true);
		}
	}

	/**
	 * 尝试查找同类型的set方法，如果不存在则直接插入
	 * 
	 * @param clz
	 * @param field
	 * @param obj
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static Object setFieldValue(Class<?> clz, Field field, Object obj, Object value) throws Exception {
		Method method = getSetterMethod(clz, field, true);
		try {
			if (method == null) {
				setAccessibleField(field);
				field.set(obj, value);
				return null;
			} else {
				return method.invoke(obj, value);
			}
		} catch (Exception e) {
			LoggerUtils.warn(ReflectUtils.class, "向对象{}，插入field={}时异常", clz.getName(), field.getName());
			throw e;
		}
	}

	public static Object getFieldValue(Class<?> clz, Object obj, Field field) throws Exception {
		Method method = getGetterMethod(clz, field, true);
		try {
			if (method == null) {
				setAccessibleField(field);
				return field.get(obj);
			} else {
				return method.invoke(obj);
			}
		} catch (Exception e) {
			LoggerUtils.warn(ReflectUtils.class, "获取对象{}中field={}时值时异常", clz.getName(), field.getName());
			throw e;
		}
	}

	public static Object setFieldValueAutoType(Class<?> clz, Field field, Object obj, String value) throws Exception {
		return setFieldValue(clz, field, obj, StringParse.defaultParse(value, field.getGenericType()));
	}

	/**
	 * 是否可以实例化
	 * 
	 * @return
	 */
	public static boolean isInstance(Class<?> clz) {
		return isInstance(clz, false);
	}

	/**
	 * 是否可以实例化
	 * 
	 * @param clz
	 * @param checkConstructor
	 *            是否检查存在无参的构造方法
	 * @return
	 */
	public static boolean isInstance(Class<?> clz, boolean checkConstructor) {
		if (clz == null) {
			return false;
		}

		if (Modifier.isAbstract(clz.getModifiers()) || Modifier.isInterface(clz.getModifiers()) || clz.isEnum()
				|| clz.isArray()) {
			return false;
		}

		if (checkConstructor) {
			try {
				clz.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Field> getFieldMap(Class<?> clz, boolean pub, boolean sup, boolean trimMap) {
		final Map<String, Field> map = new LinkedHashMap<String, Field>();
		iteratorField(clz, pub, sup, new scw.core.utils.IteratorCallback<Field>() {

			public boolean iteratorCallback(Field data) {
				if (Modifier.isStatic(data.getModifiers())) {
					return true;
				}

				map.put(data.getName(), data);
				return true;
			}
		});

		if (trimMap) {
			if (map.isEmpty()) {
				return Collections.EMPTY_MAP;
			}

			Map<String, Field> trim = new LinkedHashMap<String, Field>(map.size(), 1);
			trim.putAll(map);
			return Collections.unmodifiableMap(trim);
		}
		return map;
	}

	public static void iteratorField(Class<?> clazz, scw.core.utils.IteratorCallback<Field> iterator) {
		iteratorField(clazz, false, true, iterator);
	}

	public static void iteratorField(Class<?> clazz, boolean pub, boolean sup,
			scw.core.utils.IteratorCallback<Field> iterator) {
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Field field : pub ? clz.getFields() : clz.getDeclaredFields()) {
				field.setAccessible(true);
				if (!iterator.iteratorCallback(field)) {
					break;
				}
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
	}

	public static void iteratorMethod(Class<?> clazz, scw.core.utils.IteratorCallback<Method> iterator) {
		iteratorMethod(clazz, false, true, iterator);
	}

	public static void iteratorMethod(Class<?> clazz, boolean pub, boolean sup,
			scw.core.utils.IteratorCallback<Method> iterator) {
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Method method : pub ? clz.getMethods() : clz.getDeclaredMethods()) {
				method.setAccessible(true);
				if (!iterator.iteratorCallback(method)) {
					break;
				}
			}

			if (sup) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		}
	}

	public static Map<String, Object> getFieldValueMap(final Object bean) {
		if (bean == null) {
			return null;
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		final Class<?> clazz = ClassUtils.getUserClass(bean);
		iteratorField(clazz, new scw.core.utils.IteratorCallback<Field>() {

			public boolean iteratorCallback(Field data) {
				if (Modifier.isStatic(data.getModifiers())) {
					return true;
				}

				try {
					map.put(data.getName(), getFieldValue(clazz, bean, data));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		return map;
	}

	/**
	 * 移出指定字段
	 * 
	 * @param bean
	 * @param excludeName
	 * @return
	 */
	public static Map<String, Object> getFieldValueMapExcludeName(final Object bean, String... excludeName) {
		if (bean == null) {
			return null;
		}

		final HashSet<String> hashSet;
		if (ArrayUtils.isEmpty(excludeName)) {
			hashSet = null;
		} else {
			hashSet = new HashSet<String>(excludeName.length);
			for (String name : excludeName) {
				hashSet.add(name);
			}
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		final Class<?> clazz = ClassUtils.getUserClass(bean);
		iteratorField(clazz, new scw.core.utils.IteratorCallback<Field>() {

			public boolean iteratorCallback(Field data) {
				if (Modifier.isStatic(data.getModifiers())) {
					return true;
				}

				if (hashSet != null && hashSet.contains(data.getName())) {
					return true;
				}

				try {
					map.put(data.getName(), getFieldValue(clazz, bean, data));
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}
		});
		return map;
	}

	/**
	 * 只保留指定字段
	 * 
	 * @param object
	 * @param effectiveName
	 *            要保留的字段
	 * @return
	 */
	public static Map<String, Object> getFieldValueMapEffectiveName(final Object bean, String... effectiveName) {
		if (bean == null) {
			return null;
		}

		final HashSet<String> hashSet;
		if (ArrayUtils.isEmpty(effectiveName)) {
			hashSet = null;
		} else {
			hashSet = new HashSet<String>(effectiveName.length);
			for (String name : effectiveName) {
				hashSet.add(name);
			}
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		final Class<?> clazz = ClassUtils.getUserClass(bean);
		iteratorField(clazz, new scw.core.utils.IteratorCallback<Field>() {

			public boolean iteratorCallback(Field data) {
				if (Modifier.isStatic(data.getModifiers())) {
					return true;
				}

				if (hashSet != null && !hashSet.contains(data.getName())) {
					return true;
				}

				try {
					map.put(data.getName(), getFieldValue(clazz, bean, data));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		return map;
	}

	/**
	 * 移出指定字段
	 * 
	 * @param bean
	 * @param excludeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getObjectsFieldValueMapExcludeName(Collection<?> objects,
			String... excludeName) {
		if (CollectionUtils.isEmpty(objects)) {
			return Collections.EMPTY_LIST;
		}

		final HashSet<String> hashSet;
		if (ArrayUtils.isEmpty(excludeName)) {
			hashSet = null;
		} else {
			hashSet = new HashSet<String>(excludeName.length);
			for (String name : excludeName) {
				hashSet.add(name);
			}
		}

		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>(objects.size());
		Map<String, Field> fieldCache = null;
		for (Object obj : objects) {
			if (obj == null) {
				continue;
			}

			Class<?> clazz = ClassUtils.getUserClass(obj);
			if (fieldCache == null) {
				fieldCache = getFieldMap(clazz, false, true, false);
			}

			Map<String, Object> map = new HashMap<String, Object>(fieldCache.size(), 1);
			for (Entry<String, Field> entry : fieldCache.entrySet()) {
				if (hashSet != null && hashSet.contains(entry.getKey())) {
					continue;
				}

				Object v = null;
				try {
					v = getFieldValue(clazz, obj, entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (v == null) {
					continue;
				}

				map.put(entry.getKey(), v);
			}
			values.add(map);
		}
		return values;
	}

	/**
	 * 只保留指定字段
	 * 
	 * @param objects
	 * @param excludeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getObjectsFieldValueMapEffectiveName(Collection<?> objects,
			String... effectiveName) {
		if (CollectionUtils.isEmpty(objects)) {
			return Collections.EMPTY_LIST;
		}

		final HashSet<String> hashSet;
		if (ArrayUtils.isEmpty(effectiveName)) {
			hashSet = null;
		} else {
			hashSet = new HashSet<String>(effectiveName.length);
			for (String name : effectiveName) {
				hashSet.add(name);
			}
		}

		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>(objects.size());
		Map<String, Field> fieldCache = null;
		for (Object obj : objects) {
			if (obj == null) {
				continue;
			}

			Class<?> clazz = ClassUtils.getUserClass(obj);
			if (fieldCache == null) {
				fieldCache = getFieldMap(clazz, false, true, false);
			}

			Map<String, Object> map = new HashMap<String, Object>(fieldCache.size(), 1);
			for (Entry<String, Field> entry : fieldCache.entrySet()) {
				if (hashSet != null && !hashSet.contains(entry.getKey())) {
					continue;
				}

				Object v = null;
				try {
					v = getFieldValue(clazz, obj, entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (v == null) {
					continue;
				}

				map.put(entry.getKey(), v);
			}
			values.add(map);
		}
		return values;
	}

	public static Long getSerialVersionUID(Class<?> clz) {
		Field field;
		try {
			field = clz.getDeclaredField("serialVersionUID");
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
				field.setAccessible(true);
				return field.getLong(null);
			}
		} catch (Exception ex) {
		}
		return null;
	}
	
	public static Field[] getDeclaredFields(Class<?> clazz) {
		return getFields(clazz, true);
	}

	public static Field[] getFields(Class<?> clazz, boolean declared) {
		Field[] fields = null;
		try {
			fields = declared ? clazz.getDeclaredFields() : clazz.getFields();
		} catch (Throwable e) {
			// ingore
		}

		if (fields == null) {
			fields = new Field[0];
		}
		return fields;
	}

	public static Method[] getMethods(Class<?> clazz, boolean declared) {
		Method[] methods = null;
		try {
			methods = declared ? clazz.getDeclaredMethods() : clazz.getMethods();
		} catch (Throwable e) {
			// ingore
		}

		if (methods == null) {
			methods = new Method[0];
		}
		return methods;
	}

	public static Method[] getDeclaredMethods(Class<?> clazz) {
		return getMethods(clazz, true);
	}

	public static Object invokeStaticMethod(Class<?> clazz, String name, Class<?>[] parameterTypes, Object... params)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = clazz.getDeclaredMethod(name, parameterTypes);
		return method.invoke(null, params);
	}

	public static Object invokeStaticMethod(String className, String name, Class<?>[] parameterTypes, Object... params)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException {
		return invokeStaticMethod(Class.forName(className), name, parameterTypes, params);
	}

	public static <T> Collection<Constructor<?>> getConstructorOrderList(Class<?> clazz) {
		LinkedList<Constructor<?>> autoList = new LinkedList<Constructor<?>>();
		LinkedList<Constructor<?>> defList = new LinkedList<Constructor<?>>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			Order order = constructor.getAnnotation(Order.class);
			if (order == null) {
				defList.add(constructor);
			} else {
				autoList.add(constructor);
			}
		}

		autoList.sort(new Comparator<Constructor<?>>() {

			public int compare(Constructor<?> o1, Constructor<?> o2) {
				Order auto1 = o1.getAnnotation(Order.class);
				Order auto2 = o2.getAnnotation(Order.class);
				return CompareUtils.compare(auto1.value(), auto2.value(), true);
			}
		});

		defList.sort(new Comparator<Constructor<?>>() {

			public int compare(Constructor<?> o1, Constructor<?> o2) {
				int v1 = o1.getParameterTypes().length;
				int v2 = o2.getParameterTypes().length;

				if (v1 == v2) {
					return CompareUtils.compare(o1.getModifiers(), o2.getModifiers(), false);
				}
				return CompareUtils.compare(v1, v2, true);
			}
		});

		autoList.addAll(defList);
		return autoList;
	}
}