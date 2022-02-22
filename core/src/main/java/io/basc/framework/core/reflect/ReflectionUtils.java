package io.basc.framework.core.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;

public abstract class ReflectionUtils {
	private static final String SERIAL_VERSION_UID_FIELD_NAME = "serialVersionUID";

	private static final Method[] CLASS_PRESENT_METHODS = getMethods(Class.class).stream().filter((method) -> {
		return !Modifier.isStatic(method.getModifiers()) && !Modifier.isNative(method.getModifiers())
				&& Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("get")
				&& method.getParameterTypes().length == 0;
	}).toArray(Method[]::new);

	/**
	 * 实体成员，忽略静态的和transient修饰的
	 */
	public static final Predicate<Member> ENTITY_MEMBER = (m) -> !Modifier.isStatic(m.getModifiers())
			&& !Modifier.isTransient(m.getModifiers());

	/**
	 * 作用域比较
	 */
	public static final Comparator<Member> MEMBER_SCOPE_COMPARATOR = new MemberScopeComparator<Member>();

	/**
	 * Object对象的默认构造方法
	 */
	public static final Constructor<Object> OBJECT_CONSTRUCTOR;

	static {
		try {
			OBJECT_CONSTRUCTOR = Object.class.getConstructor();
		} catch (NoSuchMethodException e) {
			// Object对象怎么可能没有默认的构造方法
			throw new NotSupportedException(ReflectionUtils.class.getName(), e);
		}
	}

	private static final ConcurrentReferenceHashMap<Class<?>, Constructor<?>> CONSTRUCTOR_MAP = new ConcurrentReferenceHashMap<Class<?>, Constructor<?>>(
			128);

	/**
	 * 获取无参的构造方法
	 * 
	 * @see #makeAccessible(Constructor)
	 * @see Class#getDeclaredConstructor(Class...)
	 * @param <T>
	 * @param type
	 * @return
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getDeclaredConstructor(Class<T> type) {
		Assert.requiredArgument(type != null, "type");
		Constructor<T> constructor = (Constructor<T>) CONSTRUCTOR_MAP.get(type);
		if (constructor == null) {
			try {
				constructor = type.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
			}

			// 是否应该将空也存起来？
			if (constructor == null) {
				return null;
			}

			makeAccessible(constructor);
			Constructor<T> old = (Constructor<T>) CONSTRUCTOR_MAP.putIfAbsent(type, constructor);
			if (old == null) {
				// 插入成功，整理内存
				CONSTRUCTOR_MAP.purgeUnreferencedEntries();
			} else {
				constructor = old;
			}
		}
		return constructor;
	}

	/**
	 * @see Class#getConstructor(Class...)
	 * @see #getDeclaredConstructor(Class)
	 * @param <T>
	 * @param type
	 * @return
	 */
	public static <T> Constructor<T> getConstructor(Class<T> type) {
		Constructor<T> constructor = getDeclaredConstructor(type);
		return (constructor != null && Modifier.isPublic(constructor.getModifiers())) ? constructor : null;
	}

	/**
	 * @see Class#getConstructor(Class...)
	 * @param <T>
	 * @param type
	 * @param parameterTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
		if (parameterTypes == null || parameterTypes.length == 0) {
			return getConstructor(type);
		}

		try {
			return type.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
		}

		return (Constructor<T>) getConstructors(type).streamAll()
				.filter((e) -> ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes)).findFirst().orElse(null);
	}

	/**
	 * @see ClassUtils#getClass(String, ClassLoader)
	 * @see #getConstructor(Class, Class...)
	 * @param <T>
	 * @param className
	 * @param classLoader
	 * @param parameterTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(String className, @Nullable ClassLoader classLoader,
			Class<?>... parameterTypes) {
		Class<T> clazz = (Class<T>) ClassUtils.getClass(className, classLoader);
		if (clazz == null) {
			return null;
		}

		return getConstructor(clazz, parameterTypes);
	}

	/**
	 * @see ClassUtils#getClass(String, ClassLoader)
	 * @see #getDeclaredConstructor(Class, Class...)
	 * @param <T>
	 * @param className
	 * @param classLoader
	 * @param parameterTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getDeclaredConstructor(String className, @Nullable ClassLoader classLoader,
			Class<?>... parameterTypes) {
		Class<T> clazz = (Class<T>) ClassUtils.getClass(className, classLoader);
		if (clazz == null) {
			return null;
		}

		return getDeclaredConstructor(clazz, parameterTypes);
	}

	/**
	 * @see Class#getDeclaredConstructor(Class...)
	 * @param <T>
	 * @param type
	 * @param parameterTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getDeclaredConstructor(Class<T> type, Class<?>... parameterTypes) {
		if (parameterTypes == null || parameterTypes.length == 0) {
			return getDeclaredConstructor(type);
		}

		try {
			return type.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
		}

		return (Constructor<T>) getDeclaredConstructors(type).streamAll()
				.filter((e) -> ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes)).findFirst().orElse(null);
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... args) {
		makeAccessible(constructor);
		try {
			return constructor.newInstance(args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 判断此类是否可用(会静态初始化)
	 * 
	 * @param clazz
	 * @param accept
	 * @return
	 */
	public static boolean isAvailable(Class<?> clazz, @Nullable Accept<Throwable> accept) {
		try {
			for (Method method : CLASS_PRESENT_METHODS) {
				method.invoke(clazz);
			}
		} catch (Throwable e) {
			return accept == null ? false : accept.accept(e);
		}
		return true;
	}

	public static boolean isAvailable(Class<?> clazz) {
		return isAvailable(clazz, null);
	}

	public static boolean isCloneable(Object source) {
		return source != null && source instanceof Cloneable && (getCloneMethod((Cloneable) source) != null);
	}

	/**
	 * 通过反射调用clone方法
	 * 
	 * @see java.lang.Object.clone()
	 * @param source
	 * @return 如果对象不存在clone行为就返回空
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeCloneMethod(Object source) {
		if (source == null) {
			return null;
		}

		if (!(source instanceof Cloneable)) {
			return null;
		}

		Method method = getCloneMethod((Cloneable) source);
		if (method == null) {
			return null;
		}

		return (T) invoke(method, source);
	}

	/**
	 * 使用反射判断是否存在无参的构造方法(包含未公开的构造方法)
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isInstance(Class<?> clazz) {
		if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
			return false;
		}

		return getDeclaredConstructor(clazz) != null;
	}

	/**
	 * 使用反射查找无参的构造方法(包含未公开的构造方法)
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws NotSupportedException 不存在无参构造方法
	 */
	public static <T> T newInstance(Class<T> clazz) throws NotSupportedException {
		Constructor<T> constructor = getDeclaredConstructor(clazz);
		if (constructor == null) {
			throw new NotSupportedException(clazz.getName());
		}

		try {
			return constructor.newInstance();
		} catch (Exception e) {
			handleReflectionException(e);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 使用空值构造实体
	 * 
	 * @param <T>
	 * @param entityClass
	 * @return
	 * @throws NotSupportedException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceWithNullValues(Class<T> entityClass) throws NotSupportedException {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Stream<Constructor<?>> stream = ReflectionUtils.getDeclaredConstructors(entityClass).streamAll()
				.sorted(MEMBER_SCOPE_COMPARATOR).sorted(Comparator.comparingInt(Constructor::getParameterCount));
		try {
			Iterator<Constructor<?>> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Constructor<?> constructor = iterator.next();
				return (T) newInstance(constructor, new Object[constructor.getParameterCount()]);
			}
		} finally {
			stream.close();
		}
		throw new NotSupportedException(entityClass.getName());
	}

	/**
	 * 根据参数来构造实体
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param params
	 * @return
	 * @throws NotSupportedException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceWithParams(Class<T> entityClass, Object... params) throws NotSupportedException {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Assert.requiredArgument(params != null, "params");
		Stream<ExecutableMatchingResults<Constructor<?>>> stream = matchParams(
				ReflectionUtils.getDeclaredConstructors(entityClass).streamAll(), false, params);
		try {
			Iterator<ExecutableMatchingResults<Constructor<?>>> iterator = stream.iterator();
			if (iterator.hasNext()) {
				ExecutableMatchingResults<Constructor<?>> results = iterator.next();
				try {
					return (T) ReflectionUtils.newInstance(results.getExecutable(), results.getParams());
				} catch (Exception e) {
					// 忽略
				}
			}
		} finally {
			stream.close();
		}
		throw new NotSupportedException(entityClass.getName());
	}

	/**
	 * @see Class#getField(String)
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getField(name);
		} catch (NoSuchFieldException | SecurityException e) {
		}
		return null;
	}

	/**
	 * @see Class#getDeclaredField(String)
	 * @param clazz
	 * @param name
	 * @return
	 */
	@Nullable
	public static Field getDeclaredField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
		}
		return null;
	}

	/**
	 * Set the field represented by the supplied {@link Field field object} on the
	 * specified {@link Object target object} to the specified {@code value} . In
	 * accordance with {@link Field#set(Object, Object)} semantics, the new value is
	 * automatically unwrapped if the underlying field has a primitive type.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field  the field to set
	 * @param target the target object on which to set the field
	 * @param value  the value to set; may be {@code null}
	 */
	public static void set(Field field, Object target, Object value) {
		makeAccessible(field);
		try {
			field.set(target, value);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Get the field represented by the supplied {@link Field field object} on the
	 * specified {@link Object target object}. In accordance with
	 * {@link Field#get(Object)} semantics, the returned value is automatically
	 * wrapped if the underlying field has a primitive type.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field  the field to get
	 * @param target the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object get(Field field, Object target) {
		makeAccessible(field);
		try {
			return field.get(target);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and parameter types. Searches all superclasses up to {@code Object}.
	 * <p>
	 * Returns {@code null} if no {@link Method} can be found.
	 * 
	 * @param clazz      the class to introspect
	 * @param name       the name of the method
	 * @param paramTypes the parameter types of the method (may be {@code null} to
	 *                   indicate any signature)
	 * @return the Method object, or {@code null} if none found
	 */
	@Nullable
	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		return getDeclaredMethods(clazz).withAll().streamAll().filter((method) -> {
			return name.equals(method.getName()) && (paramTypes == null || ClassUtils
					.isAssignable(paramTypes == null ? new Class[0] : paramTypes, method.getParameterTypes()));
		}).findFirst().orElse(null);
	}

	/**
	 * @see ClassUtils#getClass(String, ClassLoader)
	 * @see #getDeclaredMethod(Class, String, Class...)
	 * @param className
	 * @param classLoader
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getDeclaredMethod(String className, @Nullable ClassLoader classLoader, String methodName,
			Class<?>... parameterTypes) {
		Class<?> clz = ClassUtils.getClass(className, classLoader);
		if (clz == null) {
			return null;
		}

		return getDeclaredMethod(clz, methodName, parameterTypes);
	}

	/**
	 * @see Class#getDeclaredMethod(String, Class...)
	 * @param clazz
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
		}
		return getDeclaredMethods(clazz).streamAll().filter(
				(e) -> e.getName().equals(name) && ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes))
				.findFirst().orElse(null);
	}

	/**
	 * @see ClassUtils#getClass(String, ClassLoader)
	 * @see ReflectionUtils#getMethod(Class, String, Class...)
	 * @param className
	 * @param classLoader
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod(String className, @Nullable ClassLoader classLoader, String methodName,
			Class<?>... parameterTypes) {
		Class<?> clz = ClassUtils.getClass(className, classLoader);
		if (clz == null) {
			return null;
		}

		return getMethod(clz, methodName, parameterTypes);
	}

	/**
	 * @see Class#getMethod(String, Class...)
	 * @param clazz
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
		}
		return getMethods(clazz).streamAll().filter(
				(e) -> e.getName().equals(name) && ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes))
				.findFirst().orElse(null);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object with
	 * the supplied arguments. The target object can be {@code null} when invoking a
	 * static {@link Method}.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException}.
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args   the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 */
	public static Object invoke(Method method, @Nullable Object target, Object... args) {
		makeAccessible(method);
		try {
			return method.invoke(target, args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	private static <T extends Executable> ExecutableMatchingResults<T> getExecutableMatchingResults(T executable,
			Object[] params, int minStart) {
		Class<?>[] parameterTypes = executable.getParameterTypes();
		if (parameterTypes.length == 0) {
			return new ExecutableMatchingResults<>(executable, new Object[0], 0);
		}

		Object[] cloneParams = params.clone();
		Object[] args = new Object[parameterTypes.length];
		int count = 0;
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			// 取最小值的目的是可以动态控制初始查找位置
			for (int a = Math.min(minStart, i); a < cloneParams.length; a++) {
				Object value = cloneParams[a];
				if (value == null) {
					continue;
				}

				if (ClassUtils.isAssignableValue(parameterType, value)) {
					args[i] = value;
					cloneParams[a] = null;
					count++;
					break;
				}
			}
		}
		return new ExecutableMatchingResults<>(executable, args, count);
	}

	public static <T extends Executable> Stream<ExecutableMatchingResults<T>> matchParams(Stream<T> sourceStream,
			int validParametersCount, Object... params) {
		Assert.requiredArgument(sourceStream != null, "sourceStream");
		Assert.requiredArgument(params != null, "params");
		return sourceStream.filter((e) -> e.getParameterCount() <= validParametersCount).sorted(MEMBER_SCOPE_COMPARATOR)
				.sorted((e1, e2) -> {
					Class<?>[] parameterTypes1 = e1.getParameterTypes();
					Class<?>[] parameterTypes2 = e2.getParameterTypes();
					int v = Integer.compare(parameterTypes1.length, parameterTypes2.length);
					if (v == 0) {
						int leftCount = 0;
						int rightCount = 0;
						for (int i = 0; i < parameterTypes1.length; i++) {
							if (ClassUtils.isAssignable(parameterTypes1[i], parameterTypes2[i])) {
								leftCount++;
							}

							if (ClassUtils.isAssignable(parameterTypes2[i], parameterTypes1[i])) {
								rightCount++;
							}
						}

						if (leftCount == rightCount) {
							// 参数数量相同，比较参数顺序
							ExecutableMatchingResults<T> matchingResults1 = getExecutableMatchingResults(e1, params,
									params.length);
							ExecutableMatchingResults<T> matchingResults2 = getExecutableMatchingResults(e2, params,
									params.length);
							return matchingResults1.compareTo(matchingResults2);
						}
						return leftCount - rightCount;
					}
					return -v;
				}).map((e) -> getExecutableMatchingResults(e, params, 0)).sorted();
	}

	/**
	 * 通过参数获取可以调用的{@link java.lang.reflect.Executable}
	 * 
	 * @param <T>
	 * @param sourceStream
	 * @param strict       true表示严格的验证参数(包含有效长度、类型等)
	 * @param params
	 * @return
	 */
	public static <T extends Executable> Stream<ExecutableMatchingResults<T>> matchParams(Stream<T> sourceStream,
			boolean strict, Object... params) {
		Stream<ExecutableMatchingResults<T>> stream;
		if (strict) {
			long validParametersCount = Arrays.asList(params).stream().filter((e) -> e != null).count();
			stream = matchParams(sourceStream, (int) validParametersCount, params)
					.filter((e) -> e.getMatchingResultes() == validParametersCount);
		} else {
			stream = matchParams(sourceStream, params.length, params);
		}
		return stream;
	}

	/**
	 * 通过参数获取可以调用的{@link java.lang.reflect.Executable}
	 * 
	 * @see #matchParams(Stream, boolean, Object...)
	 * @param <T>
	 * @param sourceStream
	 * @param strict       true表示严格的验证参数(包含有效长度、类型等)
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static <T extends Executable> ExecutableMatchingResults<T> getByParams(Stream<T> sourceStream,
			boolean strict, Object... params) throws NoSuchMethodException {
		Stream<ExecutableMatchingResults<T>> stream = matchParams(sourceStream, strict, params);
		try {
			return stream.findFirst().get();
		} catch (NoSuchElementException e) {
			throw (e.getLocalizedMessage() == null ? new NoSuchMethodException()
					: new NoSuchMethodException(e.getLocalizedMessage()));
		}
	}

	/**
	 * 获取重载的方法
	 * 
	 * @param <T>
	 * @param sourceClass
	 * @param methodName
	 * @param strict      {@link #findByParams(Stream, boolean, Object...)}
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static ExecutableMatchingResults<Method> getOverloadMethod(Class<?> sourceClass, String methodName,
			boolean strict, Predicate<Method> predicate, Object... args) throws NoSuchMethodException {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Stream<Method> methods = getMethods(sourceClass).withAll().streamAll()
				.filter((m) -> StringUtils.isEmpty(methodName) || methodName.equals(m.getName())).filter(predicate);
		return getByParams(methods, strict, args);
	}

	/**
	 * 根据参数调用方法(非静态方法)
	 * 
	 * @param target
	 * @param methodName
	 * @param strict     {@link #findByParams(Stream, boolean, Object...)}
	 * @param args
	 * @return
	 * @throws NoSuchMethodException 不存在对应的方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadMethod(Object target, String methodName, boolean strict, Object... args)
			throws NoSuchMethodException {
		Assert.requiredArgument(target != null, "target");
		ExecutableMatchingResults<Method> results = getOverloadMethod(target.getClass(), methodName, strict,
				(m) -> !Modifier.isStatic(m.getModifiers()), args);
		return (T) invoke(results.getExecutable(), target, results.getUnsafeParams());
	}

	/**
	 * 根据参数调用方法(静态方法)
	 * 
	 * @param sourceClass
	 * @param methodName
	 * @param strict      {@link #findByParams(Stream, boolean, Object...)}
	 * @param args
	 * @return
	 * @throws NoSuchMethodException 不存在对应的方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadMethod(Class<?> sourceClass, String methodName, boolean strict, Object... args)
			throws NoSuchMethodException {
		ExecutableMatchingResults<Method> results = getOverloadMethod(sourceClass, methodName, strict,
				(m) -> Modifier.isStatic(m.getModifiers()), args);
		return (T) invoke(results.getExecutable(), null, results.getUnsafeParams());
	}

	/**
	 * 根据参数调用构造方法
	 * 
	 * @param <T>
	 * @param sourceClass
	 * @param strict      {@link #findByParams(Stream, boolean, Object...)}
	 * @param args
	 * @return
	 * @throws NoSuchMethodException 不存在指定的构造方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadConstructor(Class<? extends T> sourceClass, boolean strict, Object... args)
			throws NoSuchMethodException {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Stream<Constructor<?>> constructors = getConstructors(sourceClass).streamAll();
		ExecutableMatchingResults<Constructor<?>> results = getByParams(constructors, strict, args);
		return (T) newInstance(results.getExecutable(), results.getUnsafeParams());
	}

	/**
	 * Handle the given reflection exception. Should only be called if no checked
	 * exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message else.
	 * 
	 * @param ex the reflection exception to handle
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	public static void handleThrowable(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		} else if (ex instanceof Exception) {
			handleReflectionException((Exception) ex);
			throw NestedExceptionUtils.shouldNeverGetHere();
		} else {
			throw new UndeclaredThrowableException(ex);
		}

	}

	/**
	 * Handle the given invocation target exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of such a root cause.
	 * Throws an IllegalStateException else.
	 * 
	 * @param ex the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}. Should
	 * only be called if no checked exception is expected to be thrown by the target
	 * method.
	 * <p>
	 * Rethrows the underlying exception cast to an {@link RuntimeException} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param ex the exception to rethrow
	 * @throws RuntimeException the rethrown exception
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}. Should
	 * only be called if no checked exception is expected to be thrown by the target
	 * method.
	 * <p>
	 * Rethrows the underlying exception cast to an {@link Exception} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param ex the exception to rethrow
	 * @throws Exception the rethrown exception (in case of a checked exception)
	 */
	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) {
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Determine whether the given method explicitly declares the given exception or
	 * one of its superclasses, which means that an exception of that type can be
	 * propagated as-is within a reflective invocation.
	 * 
	 * @param method        the declaring method
	 * @param exceptionType the exception to throw
	 * @return {@code true} if the exception can be thrown as-is; {@code false} if
	 *         it needs to be wrapped
	 */
	public static boolean declaresException(Method method, Class<?> exceptionType) {
		Assert.notNull(method, "Method must not be null");
		Class<?>[] declaredExceptions = method.getExceptionTypes();
		for (Class<?> declaredException : declaredExceptions) {
			if (declaredException.isAssignableFrom(exceptionType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether the given method is an "equals" method.
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	public static boolean isEqualsMethod(Method method) {
		if (method == null || !method.getName().equals("equals")) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == Object.class);
	}

	/**
	 * Determine whether the given method is a "hashCode" method.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public static boolean isHashCodeMethod(Method method) {
		return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
	}

	/**
	 * Determine whether the given method is a "toString" method.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
	}

	/**
	 * Determine whether the given method is originally declared by
	 * {@link java.lang.Object}.
	 */
	public static boolean isObjectMethod(Method method) {
		return getDeclaredMethod(Object.class, method.getName(), method.getParameterTypes()) != null;
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager
	 * (if active).
	 * 
	 * @param field the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if (field == null) {
			return;
		}

		// JDK 9 被弃用
		/*
		 * if (field.isAccessible()) { return; }
		 */

		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers()))) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager
	 * (if active).
	 * 
	 * @param method the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	public static void makeAccessible(Method method) {
		if (method == null) {
			return;
		}
		// JDK 9 被弃用
		/*
		 * if (method.isAccessible()) { return; }
		 */
		if ((!Modifier.isPublic(method.getModifiers())
				|| !Modifier.isPublic(method.getDeclaringClass().getModifiers()))) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM SecurityManager
	 * (if active).
	 * 
	 * @param ctor the constructor to make accessible
	 * @see java.lang.reflect.Constructor#setAccessible
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if (ctor == null) {
			return;
		}

		// JDK 9 被弃用
		/*
		 * if (ctor.isAccessible()) { return; }
		 */

		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))) {
			ctor.setAccessible(true);
		}
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
	public static <T> Object invoke(Class<T> type, Object instance, String name, Map<String, Object> parameterMap)
			throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return getDeclaredMethod(type, name).invoke(instance);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		Iterator<Method> iterator = getDeclaredMethods(type).withAll().streamAll().iterator();
		while (iterator.hasNext()) {
			Method method = iterator.next();
			if (size == method.getParameterTypes().length) {
				String[] names = ParameterUtils.getParameterNames(method);
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
					return invoke(method, instance, args);
				}
			}
		}
		throw new NoSuchMethodException(type.getName() + ", method=" + name);
	}

	/**
	 * Given a method, which may come from an interface, and a target class used in
	 * the current reflective invocation, find the corresponding target method if
	 * there is one. E.g. the method may be {@code IFoo.bar()} and the target class
	 * may be {@code DefaultFoo}. In this case, the method may be
	 * {@code DefaultFoo.bar()}. This enables attributes on that method to be found.
	 * <p>
	 * <b>NOTE:</b> In contrast to this method does <i>not</i> resolve Java 5 bridge
	 * methods automatically. Call if bridge method resolution is desirable (e.g.
	 * for obtaining metadata from the original method definition).
	 * 
	 * @param method      the method to be invoked, which may come from an interface
	 * @param targetClass the target class for the current invocation. May be
	 *                    {@code null} or may not even implement the method.
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
	 * Determine whether the given method is overridable in the given target class.
	 * 
	 * @param method      the method to check
	 * @param targetClass the target class to check against
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
	 * Return the qualified name of the given method, consisting of fully qualified
	 * interface/class name + "." + method name.
	 * 
	 * @param method the method
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method) {
		return getQualifiedMethodName(method, null);
	}

	/**
	 * Return the qualified name of the given method, consisting of fully qualified
	 * interface/class name + "." + method name.
	 * 
	 * @param method the method
	 * @param clazz  the clazz that the method is being invoked on (may be
	 *               {@code null} to indicate the method's declaring class)
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method, @Nullable Class<?> clazz) {
		Assert.notNull(method, "Method must not be null");
		return (clazz != null ? clazz : method.getDeclaringClass()).getName() + '.' + method.getName();
	}

	/**
	 * @see Class#getFields()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Field> getFields(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			if (c == Object.class) {
				return null;
			}

			Field[] fields = c.getFields();
			List<Field> list = fields == null ? Collections.emptyList() : Arrays.asList(fields);
			return list.stream();
		});
	}

	/**
	 * @see Class#getDeclaredFields()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Field> getDeclaredFields(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			if (c == Object.class) {
				return null;
			}

			Field[] fields = c.getDeclaredFields();
			List<Field> list = fields == null ? Collections.emptyList() : Arrays.asList(fields);
			return list.stream();
		});
	}

	/**
	 * @see Class#getMethods()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Method> getMethods(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			Method[] methods = c.getMethods();
			List<Method> list = methods == null ? Collections.emptyList() : Arrays.asList(methods);
			return list.stream();
		});
	}

	/**
	 * @see Class#getDeclaredMethods()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Method> getDeclaredMethods(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			Method[] methods = c.getDeclaredMethods();
			List<Method> list = methods == null ? Collections.emptyList() : Arrays.asList(methods);
			return list.stream();
		});
	}

	/**
	 * @see Class#getConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Constructor<?>> getConstructors(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			if (c == Object.class) {
				return null;
			}

			Constructor<?>[] constructors = c.getConstructors();
			List<Constructor<?>> list = constructors == null ? Collections.emptyList() : Arrays.asList(constructors);
			return list.stream();
		});
	}

	/**
	 * @see Class#getDeclaredConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static Members<Constructor<?>> getDeclaredConstructors(Class<?> sourceClass) {
		return new Members<>(sourceClass, (c) -> {
			if (c == Object.class) {
				return null;
			}

			Constructor<?>[] constructors = c.getDeclaredConstructors();
			List<Constructor<?>> list = constructors == null ? Collections.emptyList() : Arrays.asList(constructors);
			return list.stream();
		});
	}

	/**
	 * Returns {@code true} if this method is a default method; returns
	 * {@code false} otherwise.
	 *
	 * A default method is a public non-abstract instance method, that is, a
	 * non-static method with a body, declared in an interface type.
	 *
	 * @return true if and only if this method is a default method as defined by the
	 *         Java Language Specification.
	 */
	public static boolean isDefault(Method method) {
		// Default methods are public non-abstract instance methods
		// declared in an interface.
		return ((method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC)
				&& method.getDeclaringClass().isInterface();
	}

	public static boolean isSerialVersionUIDField(Field field) {
		return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())
				&& field.getName().equals(SERIAL_VERSION_UID_FIELD_NAME);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T[] values(Class<?> enumClass) {
		Assert.requiredArgument(enumClass != null, "enumClass");
		Assert.isTrue(enumClass.isEnum(), enumClass + " not is enum");

		Method method;
		try {
			method = enumClass.getMethod("values");
		} catch (Exception e) {
			return (T[]) Array.newInstance(enumClass, 0);
		}

		if (!Modifier.isStatic(method.getModifiers())) {
			// 如果不是一个静态方法就忽略
			return (T[]) Array.newInstance(enumClass, 0);
		}

		return (T[]) invoke(method, null);
	}

	public static <T> void clone(T source, T target, boolean deep) {
		Assert.requiredArgument(target != null, "target");
		if (source == null) {
			return;
		}

		clone(getDeclaredFields(target.getClass()).withAll(), source, target, deep);
	}

	public static <T> void clone(Members<Field> members, T source, T target, boolean deep) {
		Assert.requiredArgument(members != null, "members");
		if (source == null || target == null) {
			return;
		}

		members.streamAll().filter(ENTITY_MEMBER).forEach((f) -> {
			try {
				Object value = get(f, source);
				if (value == source) {
					value = target;
				} else {
					value = ObjectUtils.clone(value, deep);
				}
				set(f, target, value);
			} catch (Exception e) {
				throw new IllegalStateException("Should never get here", e);
			}
		});
	}

	/**
	 * @see ReflectionApi#newInstance(Class)
	 * @param <T>
	 * @param members
	 * @param source
	 * @param deep    对集合的操作
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(Members<Field> members, T source, boolean deep) {
		Assert.requiredArgument(members != null, "members");
		if (source == null) {
			return null;
		}

		T target = (T) ReflectionApi.newInstance(source.getClass());
		clone(members, source, target, deep);
		return target;
	}

	public static <T> T clone(T source, boolean deep) {
		if (source == null) {
			return null;
		}

		if (!deep) {
			T target = invokeCloneMethod(source);
			if (target != null) {
				return target;
			}
		}
		return clone(getDeclaredFields(source.getClass()).withAll(), source, deep);
	}

	public static Method getCloneMethod(Cloneable source) {
		if (source == null) {
			return null;
		}

		Method method = findMethod(source.getClass(), "clone");
		if (method == null) {
			return null;
		}

		if (ClassUtils.isAssignableValue(method.getReturnType(), source)) {
			return method;
		}
		return null;
	}

	/**
	 * 浅克隆(如果对象实现了Cloneable接口,那么会调用clone方法)
	 * 
	 * @param source
	 * @return
	 */
	public static <T> T clone(T source) {
		return clone(source, false);
	}

	public static <M extends Member> Members<M> getEntityMembers(Class<?> entityClass,
			Processor<Class<?>, M[], ? extends RuntimeException> processor) {
		return new Members<M>(entityClass, (c) -> Arrays.asList(processor.process(c)).stream().filter(ENTITY_MEMBER));
	}

	public static <T> String toString(Members<Field> members, T entity, boolean deep) {
		Assert.requiredArgument(members != null, "members");
		if (entity == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(members.getSourceClass().getSimpleName());
		builder.append('(');
		Iterator<Field> iterator = members.streamAll().filter(ENTITY_MEMBER).iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			builder.append(field.getName());
			builder.append('=');
			Object value = get(field, entity);
			if (value == entity) {
				builder.append("(this)");
			} else {
				builder.append(ObjectUtils.toString(value, deep));
			}
			if (iterator.hasNext()) {
				builder.append(',').append(' ');
			}
		}
		builder.append(')');
		return builder.toString();
	}

	private static <T> void toString(StringBuilder sb, Class<? extends T> entityClass, T entity, boolean deep) {
		if (entity == null) {
			return;
		}

		sb.append(entityClass.getSimpleName());
		sb.append('(');
		Iterator<Field> iterator = getDeclaredFields(entityClass).stream().filter(ENTITY_MEMBER).iterator();
		Class<?> superclass = entityClass.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			sb.append("super=");
			toString(sb, superclass, entity, deep);
			if (iterator.hasNext()) {
				sb.append(',').append(' ');
			}
		}

		while (iterator.hasNext()) {
			Field field = iterator.next();
			sb.append(field.getName());
			sb.append('=');
			Object value = get(field, entity);
			if (value == entity) {
				sb.append("(this)");
			} else {
				sb.append(ObjectUtils.toString(value, deep));
			}
			if (iterator.hasNext()) {
				sb.append(',').append(' ');
			}
		}
		sb.append(')');
	}

	public static <T> String toString(Class<? extends T> entityClass, T entity, boolean deep) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		StringBuilder sb = new StringBuilder();
		toString(sb, entityClass, entity, deep);
		return sb.toString();
	}

	public static <T> String toString(Class<? extends T> entityClass, T entity) {
		return toString(entityClass, entity, true);
	}

	public static String toString(Object entity, boolean deep) {
		if (entity == null) {
			return null;
		}

		return toString(entity.getClass(), entity, deep);
	}

	public static String toString(Object entity) {
		return toString(entity, true);
	}

	/**
	 * @see #ENTITY_MEMBER
	 * @param <T>
	 * @param <E>
	 * @param members
	 * @param left
	 * @param right
	 * @param deep
	 * @return
	 */
	public static <T, E> boolean equals(Members<Field> members, T left, T right, boolean deep) {
		Assert.requiredArgument(members != null, "members");
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		Iterator<Field> iterator = members.streamAll().filter(ENTITY_MEMBER).iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			if (!ObjectUtils.equals(get(field, left), get(field, right), deep)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean equals(Members<Field> members, T left, T right) {
		return equals(members, left, right, true);
	}

	public static <T> boolean equals(Class<? extends T> entityClass, T left, T right, boolean deep) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		return equals(getDeclaredFields(entityClass).withSuperclass(), left, right, deep);
	}

	public static <T> boolean equals(Class<? extends T> entityClass, T left, T right) {
		return equals(entityClass, left, right, true);
	}

	public static <T> boolean equals(T left, T right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		return equals(left.getClass(), left, right, deep);
	}

	public static <T> boolean equals(T left, T right) {
		return equals(left, right, true);
	}

	/**
	 * @see #ENTITY_MEMBER
	 * @param <E>
	 * @param members
	 * @param entity
	 * @param deep
	 * @return
	 */
	public static int hashCode(Members<Field> members, Object entity, boolean deep) {
		Assert.requiredArgument(members != null, "members");
		if (entity == null) {
			return 0;
		}

		int hashCode = 1;
		Iterator<Field> iterator = members.streamAll().filter(ENTITY_MEMBER).iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			hashCode = 31 * hashCode + ObjectUtils.hashCode(get(field, entity), deep);
		}
		return hashCode;
	}

	public static int hashCode(Members<Field> members, Object entity) {
		return hashCode(members, entity, true);
	}

	public static <T> int hashCode(Class<? extends T> entityClass, T entity, boolean deep) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (entity == null) {
			return 0;
		}
		return hashCode(getDeclaredFields(entityClass).withSuperclass(), entity, deep);
	}

	public static <T> int hashCode(Class<? extends T> entityClass, T entity) {
		return hashCode(entityClass, entity, true);
	}

	public static int hashCode(Object entity, boolean deep) {
		if (entity == null) {
			return 0;
		}
		return hashCode(entity.getClass(), entity, deep);
	}

	public static int hashCode(Object entity) {
		return hashCode(entity, true);
	}

	/**
	 * Determine whether the given method is declared by the user or at least
	 * pointing to a user-declared method.
	 * <p>
	 * Checks {@link Method#isSynthetic()} (for implementation methods) as well as
	 * the {@code GroovyObject} interface (for interface methods; on an
	 * implementation class, implementations of the {@code GroovyObject} methods
	 * will be marked as synthetic anyway). Note that, despite being synthetic,
	 * bridge methods ({@link Method#isBridge()}) are considered as user-level
	 * methods since they are eventually pointing to a user-declared generic method.
	 * 
	 * @param method the method to check
	 * @return {@code true} if the method can be considered as user-declared; [@code
	 *         false} otherwise
	 */
	public static boolean isUserLevelMethod(Method method) {
		Assert.notNull(method, "Method must not be null");
		return (method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method)));
	}

	private static boolean isGroovyObjectMethod(Method method) {
		return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
	}
}
