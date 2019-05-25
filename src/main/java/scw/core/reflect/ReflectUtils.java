package scw.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;
import scw.core.exception.NotFoundException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.Assert;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ReflectionUtils;
import scw.core.utils.StringUtils;

public final class ReflectUtils {
	private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

	private ReflectUtils() {
	};

	private static volatile Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<Class<?>, Map<String, Field>>();

	public static <T, V> void setProperties(Class<T> type, T bean,
			Map<String, V> properties, PropertyMapper<V> mapper) {
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
					value = mapper.mapper(entry.getKey(), entry.getValue(),
							type);
					field.set(bean, value);
				} catch (Exception e) {
					logger.error(
							"向对象" + type.getName() + "，插入name="
									+ entry.getKey() + ",value="
									+ entry.getValue() + "时异常", e);
				}
				continue;
			}

			for (Method method : methods) {
				Object value;
				try {
					value = mapper.mapper(entry.getKey(), entry.getValue(),
							method.getParameterTypes()[0]);
					method.invoke(bean, value);
				} catch (Exception e) {
					logger.error(
							"向对象" + type.getName() + "，插入name="
									+ entry.getKey() + ",value="
									+ entry.getValue() + "时异常(调用set方法)", e);
				}
			}
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type,
			boolean isPublic) {
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

			if (constructor != null
					&& !Modifier.isPublic(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
		}
		return constructor;
	}

	public static <T> Constructor<T> getConstructor(Class<T> type,
			boolean isPublic, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Constructor<T> constructor;
		if (isPublic) {
			constructor = type.getConstructor(parameterTypes);
		} else {
			constructor = type.getDeclaredConstructor(parameterTypes);
			if (!Modifier.isPublic(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
		}
		return constructor;
	}

	public static Constructor<?> getConstructor(String className,
			boolean isPublic, Class<?>... parameterTypes)
			throws NoSuchMethodException, ClassNotFoundException {
		return getConstructor(Class.forName(className), isPublic,
				parameterTypes);
	}

	public static <T> Constructor<T> getConstructor(Class<T> type,
			boolean isPublic, String... parameterTypeNames)
			throws NoSuchMethodException, ClassNotFoundException {
		return getConstructor(type, isPublic,
				ClassUtils.forName(parameterTypeNames));
	}

	public static Constructor<?> getConstructor(String className,
			boolean isPublic, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException {
		return getConstructor(Class.forName(className), isPublic,
				ClassUtils.forName(className));
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> findConstructor(Class<T> type,
			boolean isPublic, Class<?>... parameterTypes) {
		for (Constructor<?> constructor : isPublic ? type.getConstructors()
				: type.getDeclaredConstructors()) {
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
					if (!isPublic
							&& !Modifier.isPublic(constructor.getModifiers())) {
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
	public static <T> Constructor<T> findConstructorByParameters(Class<T> type,
			boolean isPublic, Object... params) {
		for (Constructor<?> constructor : isPublic ? type.getConstructors()
				: type.getDeclaredConstructors()) {
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
					if (!isPublic
							&& !Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					return (Constructor<T>) constructor;
				}
			}
		}
		return null;
	}

	/**
	 * 根据参数名来调用构造方法
	 * 
	 * @param type
	 * @param isPublic
	 * @param parameterMap
	 * @return
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type, boolean isPublic,
			Map<String, Object> parameterMap) throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return getConstructor(type, isPublic).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		for (Constructor<?> constructor : isPublic ? type.getConstructors()
				: type.getDeclaredConstructors()) {
			if (size == constructor.getParameterTypes().length) {
				String[] names = ClassUtils.getParameterName(constructor);
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
					if (!Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					try {
						return (T) constructor.newInstance(args);
					} catch (Exception e) {
						new RuntimeException(e);
					}
					break;
				}
			}
		}

		throw new NoSuchMethodException(type.getName());
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
	public static <T> Object invoke(Class<T> type, Object instance,
			String name, boolean isPublic, Map<String, Object> parameterMap)
			throws NoSuchMethodException {
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
		for (Method method : isPublic ? type.getMethods() : type
				.getDeclaredMethods()) {
			if (size == method.getParameterTypes().length) {
				String[] names = ClassUtils.getParameterName(method);
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

	public static Method getMethod(Class<?> clazz, boolean isPublic,
			String name, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Method method;
		if (isPublic) {
			method = clazz.getMethod(name, parameterTypes);
		} else {
			method = clazz.getDeclaredMethod(name, parameterTypes);
			if (!Modifier.isPublic(clazz.getModifiers())
					|| !Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
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
	public static boolean hasAtLeastOneMethodWithName(Class<?> clazz,
			String methodName) {
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
		return (clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(
				clazz.getSuperclass(), methodName));
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
	public static Method getMostSpecificMethod(Method method,
			Class<?> targetClass) {
		if (method != null && isOverridable(method, targetClass)
				&& targetClass != null
				&& !targetClass.equals(method.getDeclaringClass())) {
			try {
				if (Modifier.isPublic(method.getModifiers())) {
					try {
						return targetClass.getMethod(method.getName(),
								method.getParameterTypes());
					} catch (NoSuchMethodException ex) {
						return method;
					}
				} else {
					Method specificMethod = ReflectionUtils.findMethod(
							targetClass, method.getName(),
							method.getParameterTypes());
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
		if (Modifier.isPublic(method.getModifiers())
				|| Modifier.isProtected(method.getModifiers())) {
			return true;
		}
		return ClassUtils.getPackageName(method.getDeclaringClass()).equals(
				ClassUtils.getPackageName(targetClass));
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

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> type) {
		try {
			return (T) getConstructor(type, false).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("无法实例化：" + type.getName(), e);
		}
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T obj, boolean ignoreTransient) {
		try {
			return clone(obj, true, ignoreTransient, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object cloneArray(Class<?> type, Object array,
			boolean ignoreStatic, boolean ignoreTransient,
			boolean invokeCloneableMethod) throws Exception {
		int size = Array.getLength(array);
		Object newArr = Array.newInstance(type.getComponentType(), size);
		for (int i = 0; i < size; i++) {
			Array.set(
					newArr,
					i,
					clone(Array.get(array, i), ignoreStatic, ignoreTransient,
							invokeCloneableMethod));
		}
		return newArr;
	}

	private static Object cloneObject(Class<?> type, Object obj,
			boolean ignoreStatic, boolean ignoreTransient,
			boolean invokeCloneableMethod) throws Exception {
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			return obj;
		}

		Constructor<?> constructor = getConstructor(type, false);
		if (constructor == null) {
			return obj;
		}

		Object t = constructor.newInstance();
		Class<?> clazz = type;
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (ignoreStatic && Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (ignoreTransient
						&& Modifier.isTransient(field.getModifiers())) {
					continue;
				}

				field.setAccessible(true);
				Object v = field.get(obj);
				if (v == null) {
					continue;
				}

				if (!field.getType().isPrimitive() && !field.getType().isEnum()) {
					if (field.getType().isArray()) {
						v = cloneArray(field.getType(), v, ignoreStatic,
								ignoreTransient, invokeCloneableMethod);
					} else {
						v = clone(v, ignoreStatic, ignoreTransient,
								invokeCloneableMethod);
					}
				}
				field.set(Modifier.isStatic(field.getModifiers()) ? null : t, v);
			}
			clazz = clazz.getSuperclass();
		}
		return t;
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @param ignoreStatic
	 * @param ignoreTransient
	 * @param invokeCloneMethod
	 *            如果对象实现了java.lang.Cloneable接口，是否反射调用clone方法
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T obj, boolean ignoreStatic,
			boolean ignoreTransient, boolean invokeCloneableMethod)
			throws Exception {
		if (obj == null) {
			return null;
		}

		if (obj instanceof scw.core.Cloneable) {
			return (T) ((scw.core.Cloneable) obj).clone();
		}

		Class<?> type = obj.getClass();
		if (type.isPrimitive() || type.isEnum()) {
			return obj;
		} else if (type.isArray()) {
			return (T) cloneArray(type, obj, ignoreStatic, ignoreTransient,
					invokeCloneableMethod);
		} else if (invokeCloneableMethod && obj instanceof Cloneable) {
			try {
				return (T) getMethod(type, false, "clone").invoke(obj);
			} catch (NoSuchMethodException e) {
			}
		}

		return (T) cloneObject(type, obj, ignoreStatic, ignoreTransient,
				invokeCloneableMethod);
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

	public static Method findGetterMethod(Class<?> clazz, String fieldName,
			boolean sup) {
		Method find = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Method method : clz.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 0) {
					continue;
				}

				if (ClassUtils.isBooleanType(method.getReturnType())) {
					String methodNameSuffix = fieldName;
					if (fieldName.startsWith("is")) {
						methodNameSuffix = fieldName.substring(2);
					}

					if (method.getName().equals(
							"is"
									+ StringUtils.toUpperCase(methodNameSuffix,
											0, 1))) {
						find = method;
					} else if (method.getName().equals(
							"is" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}

					if (find != null && fieldName.startsWith("is")) {
						logger.warn("Boolean类型的字段不应该以is开头,class:{},field:{}",
								clz.getName(), fieldName);
					}
				} else {
					if (method.getName().equals(
							"get" + StringUtils.toUpperCase(fieldName, 0, 1))) {
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

	public static Method[] findSetterMethods(Class<?> clazz, String fieldName,
			boolean sup) {
		LinkedList<Method> methods = new LinkedList<Method>();
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Method method : clz.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}

				Method find = null;
				if (ClassUtils.isBooleanType(method.getParameterTypes()[0])) {
					String methodNameSuffix = fieldName;
					if (fieldName.startsWith("is")) {
						methodNameSuffix = fieldName.substring(2);
					}

					if (method.getName().equals(
							"set"
									+ StringUtils.toUpperCase(methodNameSuffix,
											0, 1))) {
						find = method;
					} else if (method.getName().equals(
							"set" + StringUtils.toUpperCase(fieldName, 0, 1))) {
						find = method;
					}

					if (find != null && fieldName.startsWith("is")) {
						logger.warn("Boolean类型的字段不应该以is开头,class:{},field:{}",
								clz.getName(), fieldName);
					}
				} else {
					if (method.getName().equals(
							"set" + StringUtils.toUpperCase(fieldName, 0, 1))) {
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
		return methods.isEmpty() ? null : methods.toArray(new Method[methods
				.size()]);
	}

	public static Method findSetterMethod(Class<?> clz, String fieldName,
			boolean sup) {
		Method[] methods = findSetterMethods(clz, fieldName, sup);
		return methods == null ? null : methods[0];
	}

	public static Method getGetterMethod(Class<?> clazz, Field field,
			boolean sup) {
		Method getter = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			if (ClassUtils.isBooleanType(field.getType())) {
				String methodNameSuffix = field.getName();
				if (methodNameSuffix.startsWith("is")) {
					logger.warn("Boolean类型的字段不应该以is开头,class:{},field:{}",
							clz.getName(), methodNameSuffix);
					methodNameSuffix = methodNameSuffix.substring(2);
				}
				try {
					getter = clz.getDeclaredMethod("is"
							+ StringUtils.toUpperCase(methodNameSuffix, 0, 1));
				} catch (NoSuchMethodException e1) {
					try {
						getter = clz
								.getDeclaredMethod("is"
										+ StringUtils.toUpperCase(
												field.getName(), 0, 1));
					} catch (NoSuchMethodException e) {
					}
				}
			} else {
				try {
					getter = clz.getDeclaredMethod("get"
							+ StringUtils.toUpperCase(field.getName(), 0, 1));
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

	public static Method getSetterMethod(Class<?> clazz, Field field,
			boolean sup) {
		Method setter = null;
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			if (ClassUtils.isBooleanType(field.getType())) {
				String methodNameSuffix = field.getName();
				if (methodNameSuffix.startsWith("is")) {
					logger.warn("Boolean类型的字段不应该以is开头,class:{},field:{}",
							clz.getName(), methodNameSuffix);
					methodNameSuffix = methodNameSuffix.substring(2);
				}

				try {
					setter = clz.getDeclaredMethod(
							"set"
									+ StringUtils.toUpperCase(methodNameSuffix,
											0, 1), field.getType());
				} catch (NoSuchMethodException e1) {
					try {
						setter = clz.getDeclaredMethod(
								"set"
										+ StringUtils.toUpperCase(
												field.getName(), 0, 1),
								field.getType());
					} catch (NoSuchMethodException e) {
					}
				}
			} else {
				try {
					setter = clz.getDeclaredMethod(
							"set"
									+ StringUtils.toUpperCase(field.getName(),
											0, 1), field.getType());
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
	public static <T> Collection<T> getFieldCollection(Collection<?> objList,
			String fieldName) {
		if (CollectionUtils.isEmpty(objList) || StringUtils.isEmpty(fieldName)) {
			return Collections.EMPTY_LIST;
		}

		LinkedList<T> list = new LinkedList<T>();
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
	public static <K, V> Map<K, V> listToMap(String keyFieldName,
			Iterable<V> list) {
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
					throw new NotFoundException("list转map时无法在实体中找到此字段："
							+ keyFieldName);
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
				throw new AlreadyExistsException("list转map时发现已经存在相同的key[" + fv
						+ "], fieldName=" + keyFieldName);
			}

			map.put(fv, v);
		}

		if (map == null) {
			return Collections.EMPTY_MAP;
		}
		return map;
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
	public static Object setFieldValue(Class<?> clz, Field field, Object obj,
			Object value) throws Exception {
		Method method = getSetterMethod(clz, field, true);
		if (method == null) {
			field.set(obj, value);
			return null;
		} else {
			return method.invoke(obj, value);
		}
	}

	public static Object setFieldValueAutoType(Class<?> clz, Field field,
			Object obj, String value) throws Exception {
		return ReflectUtils.setFieldValue(clz, field, obj,
				StringUtils.conversion(value, field.getType()));
	}

	/**
	 * 是否可以实例化
	 * 
	 * @return
	 */
	public static boolean isInstance(Class<?> clz) {
		return !(Modifier.isAbstract(clz.getModifiers()) || Modifier
				.isInterface(clz.getModifiers()));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Field> getFieldMapUseCache(Class<?> clazz) {
		Map<String, Field> map = fieldCache.get(clazz);
		if (map == null) {
			synchronized (fieldCache) {
				map = fieldCache.get(clazz);
				if (map == null) {
					Field[] fields = clazz.getDeclaredFields();
					if (fields.length == 0) {
						map = Collections.EMPTY_MAP;
					} else {
						map = new HashMap<String, Field>(fields.length, 1);
						for (Field field : fields) {
							field.setAccessible(true);
							if (ClassUtils.isBooleanType(field.getType())) {
								if (field.getName().startsWith("is")) {
									logger.warn(
											"Boolean类型的字段不应该以is开头,class:{},field:{}",
											clazz.getName(), field.getName());
								}
							}
							map.put(field.getName(), field);
						}
					}
					fieldCache.put(clazz, map);
				}
			}
		}

		if (map.isEmpty()) {
			return map;
		}
		return Collections.unmodifiableMap(map);
	}

	public static Field getFieldUseCache(Class<?> clazz, String fieldName,
			boolean searchSuper) {
		if (searchSuper) {
			Class<?> clz = clazz;
			while (clz != null && clz != Object.class) {
				Field field = getFieldMapUseCache(clazz).get(fieldName);
				if (field == null) {
					clz = clz.getSuperclass();
				}

				return field;
			}
			return null;
		} else {
			return getFieldMapUseCache(clazz).get(fieldName);
		}
	}

}