package scw.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	public static <T, V> void setProperties(Class<T> type, T bean,
			Map<String, V> properties, boolean isPublicMethod,
			SetterMapper<V> mapper) {
		if (properties == null || properties.isEmpty()) {
			return;
		}

		for (Entry<String, V> entry : properties.entrySet()) {
			String methodName = "set"
					+ StringUtils.toUpperCase(entry.getKey(), 0, 1);
			for (java.lang.reflect.Method method : isPublicMethod ? type
					.getMethods() : type.getDeclaredMethods()) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}

				if (!method.getName().equals(methodName)) {
					continue;
				}

				Object v;
				try {
					v = mapper.mapper(bean, method, entry.getKey(),
							entry.getValue(), method.getParameterTypes()[0]);
					method.setAccessible(true);
					method.invoke(bean, v);
				} catch (Throwable e) {
					logger.error(
							"向对象" + type.getName() + "，插入name="
									+ entry.getKey() + ",value="
									+ entry.getValue() + "时异常", e);
				}
			}
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type,
			boolean isPublic) throws NoSuchMethodException {
		Constructor<T> constructor;
		if (isPublic) {
			constructor = type.getConstructor();
		} else {
			constructor = type.getDeclaredConstructor();
			if (!Modifier.isPublic(constructor.getModifiers())) {
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
			throw new RuntimeException(e);
		}
	}

	public static String getGetterFieldName(Method method) {
		String name = method.getName();
		if (name.startsWith("is")) {
			name = name.substring(2);
		} else if (name.startsWith("get")) {
			name = name.substring(3);
		} else {
			return null;
		}

		return StringUtils.toLowerCase(name, 0, 1);
	}

	public static String getSetterFieldName(Method method) {
		String name = method.getName();
		if (name.startsWith("set")) {
			name = name.substring(1);
		}
		return StringUtils.toLowerCase(name, 0, 1);
	}

	/**
	 * 必须要存在默认的构造方法
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T obj) {
		try {
			return clone(obj, true, true, true);
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

		Constructor<?> constructor = null;
		try {
			constructor = getConstructor(type, false);
		} catch (NoSuchMethodException e) {
		}

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

	public static Map<String, Object> getter(Object instance) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Method method : instance.getClass().getMethods()) {
			if (method.getParameterTypes().length != 0) {
				continue;
			}

			String name = getGetterFieldName(method);
			if (name == null) {
				continue;
			}

			try {
				map.put(name, method.invoke(instance));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}
}