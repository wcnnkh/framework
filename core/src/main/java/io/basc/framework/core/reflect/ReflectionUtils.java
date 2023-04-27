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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Source;
import io.basc.framework.util.StringUtils;

public abstract class ReflectionUtils {
	private static final Method[] CLASS_PRESENT_METHODS = getMethods(Class.class).getElements().filter((method) -> {
		return !Modifier.isStatic(method.getModifiers()) && !Modifier.isNative(method.getModifiers())
				&& Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("get")
				&& method.getParameterTypes().length == 0;
	}).toArray(Method[]::new);

	private static final ConcurrentReferenceHashMap<Class<?>, Constructor<?>> CONSTRUCTOR_MAP = new ConcurrentReferenceHashMap<Class<?>, Constructor<?>>(
			128);

	/**
	 * 实体成员，忽略静态的
	 */
	public static final Predicate<Member> ENTITY_MEMBER = (m) -> !Modifier.isStatic(m.getModifiers());

	private static volatile Logger logger;

	/**
	 * 作用域比较
	 */
	public static final Comparator<Member> MEMBER_SCOPE_COMPARATOR = new MemberScopeComparator<Member>();

	/**
	 * Object对象的默认构造方法
	 */
	public static final Constructor<Object> OBJECT_CONSTRUCTOR;

	private static final String SERIAL_VERSION_UID_FIELD_NAME = "serialVersionUID";

	static {
		try {
			OBJECT_CONSTRUCTOR = Object.class.getConstructor();
		} catch (NoSuchMethodException e) {
			// Object对象怎么可能没有默认的构造方法
			throw new UnsupportedException(ReflectionUtils.class.getName(), e);
		}
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
	 * 通过参数获取可以调用的{@link java.lang.reflect.Executable}
	 * 
	 * @see #matchParams(Stream, boolean, Object...)
	 * @param <T>
	 * @param elements
	 * @param strict   true表示严格的验证参数(包含有效长度、类型等)
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static <T extends Executable> ExecutableMatchingResults<T> getByParams(Elements<T> elements, boolean strict,
			Object... params) throws NoSuchMethodException {
		Elements<ExecutableMatchingResults<T>> stream = matchParams(elements, strict, params);
		try {
			return stream.findFirst().get();
		} catch (NoSuchElementException e) {
			throw (e.getLocalizedMessage() == null ? new NoSuchMethodException()
					: new NoSuchMethodException(e.getLocalizedMessage()));
		}
	}

	public static Method getCloneMethod(Cloneable source) {
		if (source == null) {
			return null;
		}

		Method method = getDeclaredMethods(source.getClass()).find("clone");
		if (method == null) {
			return null;
		}

		if (ClassUtils.isAssignableValue(method.getReturnType(), source)) {
			return method;
		}
		return null;
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

		return (Constructor<T>) getConstructors(type)
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
	 * @see Class#getConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static Elements<Constructor<?>> getConstructors(Class<?> sourceClass) {
		Constructor<?>[] constructors = sourceClass.getConstructors();
		return Elements.forArray(constructors);
	}

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

		return (Constructor<T>) getDeclaredConstructors(type)
				.filter((e) -> ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes)).findFirst().orElse(null);
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
	 * @see Class#getDeclaredConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static Elements<Constructor<?>> getDeclaredConstructors(Class<?> sourceClass) {
		return Elements.of(() -> Arrays.asList(sourceClass.getDeclaredConstructors()).iterator());
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
	 * @see Class#getDeclaredFields()
	 * @param sourceClass
	 * @return
	 */
	public static Fields getDeclaredFields(Class<?> sourceClass) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new Fields(sourceClass, Class::getDeclaredFields);
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
		return getDeclaredMethods(clazz).all().getElements().filter(
				(e) -> e.getName().equals(name) && ClassUtils.isAssignable(e.getParameterTypes(), parameterTypes))
				.first();
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
	 * @see Class#getDeclaredMethods()
	 * @param sourceClass
	 * @return
	 */
	public static Methods getDeclaredMethods(Class<?> sourceClass) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new Methods(sourceClass, Class::getDeclaredMethods);
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
	 * @see Class#getFields()
	 * @param sourceClass
	 * @return
	 */
	public static Fields getFields(Class<?> sourceClass) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new Fields(sourceClass, Class::getFields);
	}

	private static Logger getLogger() {
		if (logger == null) {
			synchronized (ReflectionUtils.class) {
				if (logger == null) {
					logger = LoggerFactory.getLogger(ReflectionUtils.class);
				}
			}
		}
		return logger;
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
		return getMethods(clazz).find(name, parameterTypes);
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
	 * @see Class#getMethods()
	 * @param sourceClass
	 * @return
	 */
	public static Methods getMethods(Class<?> sourceClass) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new Methods(sourceClass, Class::getMethods);
	}

	public static ExecutableMatchingResults<Method> getOverloadMethod(Class<?> sourceClass, String methodName,
			boolean strict, Predicate<Method> predicate, Object... args) throws NoSuchMethodException {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Elements<Method> methods = getMethods(sourceClass).all().getElements()
				.filter((m) -> StringUtils.isEmpty(methodName) || methodName.equals(m.getName())).filter(predicate);
		return getByParams(methods, strict, args);
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
			throw Assert.shouldNeverGetHere();
		} else {
			throw new UndeclaredThrowableException(ex);
		}
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

	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadConstructor(Class<? extends T> sourceClass, boolean strict, Object... args)
			throws NoSuchMethodException {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Elements<Constructor<?>> constructors = getConstructors(sourceClass);
		ExecutableMatchingResults<Constructor<?>> results = getByParams(constructors, strict, args);
		return (T) newInstance(results.getExecutable(), results.getUnsafeParams());
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadMethod(Class<?> sourceClass, String methodName, boolean strict, Object... args)
			throws NoSuchMethodException {
		ExecutableMatchingResults<Method> results = getOverloadMethod(sourceClass, methodName, strict,
				(m) -> Modifier.isStatic(m.getModifiers()), args);
		return (T) invoke(results.getExecutable(), null, results.getUnsafeParams());
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeOverloadMethod(Object target, String methodName, boolean strict, Object... args)
			throws NoSuchMethodException {
		Assert.requiredArgument(target != null, "target");
		ExecutableMatchingResults<Method> results = getOverloadMethod(target.getClass(), methodName, strict,
				(m) -> !Modifier.isStatic(m.getModifiers()), args);
		return (T) invoke(results.getExecutable(), target, results.getUnsafeParams());
	}

	public static boolean isAvailable(Class<?> clazz) {
		return isAvailable(clazz, () -> getLogger());
	}

	public static <E extends Throwable> boolean isAvailable(Class<?> clazz,
			@Nullable Source<? extends Logger, ? extends E> loggerSource) throws E {
		return isAvailable(clazz, loggerSource == null ? null : (e) -> {
			Logger logger = loggerSource.get();
			if (logger == null) {
				logger = getLogger();
			}
			isAvailableLogger(clazz, logger, e);
		});
	}

	/**
	 * 判断此类是否可用(会静态初始化)
	 * 
	 * @param clazz
	 * @param accept
	 * @return
	 * @throws E
	 */
	public static <E extends Throwable> boolean isAvailable(Class<?> clazz,
			@Nullable ConsumeProcessor<Throwable, E> accept) throws E {
		try {
			for (Method method : CLASS_PRESENT_METHODS) {
				method.invoke(clazz);
			}
		} catch (Throwable e) {
			if (accept != null) {
				accept.process(e);
			}
			return false;
		}
		return true;
	}

	public static boolean isAvailable(Class<?> clazz, @Nullable Logger logger) {
		return isAvailable(clazz, logger == null ? null : (e) -> isAvailableLogger(clazz, logger, e));
	}

	private static void isAvailableLogger(Class<?> clazz, Logger logger, Throwable e) {
		if (logger.isTraceEnabled()) {
			logger.trace(e, "This class[{}] cannot be included because:", clazz.getName());
		} else if (logger.isDebugEnabled()) {
			logger.debug("This class[{}] cannot be included because {}: {}", clazz.getName(),
					NestedExceptionUtils.getRootCause(e).getClass(), NestedExceptionUtils.getNonEmptyMessage(e, false));
		}
	}

	public static boolean isCloneable(Object source) {
		return source != null && source instanceof Cloneable && (getCloneMethod((Cloneable) source) != null);
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
	 * Determine whether the given method is originally declared by
	 * {@link java.lang.Object}.
	 */
	public static boolean isObjectMethod(Method method) {
		return getDeclaredMethod(Object.class, method.getName(), method.getParameterTypes()) != null;
	}

	public static boolean isSerialVersionUIDField(Field field) {
		return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())
				&& field.getName().equals(SERIAL_VERSION_UID_FIELD_NAME);
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

		if (!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
			ctor.setAccessible(true);
		}
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
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) {
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
		if (!Modifier.isPublic(method.getModifiers())
				|| !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
			method.setAccessible(true);
		}
	}

	/**
	 * 通过参数获取可以调用的{@link java.lang.reflect.Executable}
	 * 
	 * @param <T>
	 * @param elements
	 * @param strict   true表示严格的验证参数(包含有效长度、类型等)
	 * @param params
	 * @return
	 */
	public static <T extends Executable> Elements<ExecutableMatchingResults<T>> matchParams(Elements<T> elements,
			boolean strict, Object... params) {
		Elements<ExecutableMatchingResults<T>> stream;
		if (strict) {
			long validParametersCount = Arrays.asList(params).stream().filter((e) -> e != null).count();
			stream = matchParams(elements, (int) validParametersCount, params)
					.filter((e) -> e.getMatchingResultes() == validParametersCount);
		} else {
			stream = matchParams(elements, params.length, params);
		}
		return stream;
	}

	public static <T extends Executable> Elements<ExecutableMatchingResults<T>> matchParams(Elements<T> elements,
			int validParametersCount, Object... params) {
		Assert.requiredArgument(elements != null, "elements");
		Assert.requiredArgument(params != null, "params");
		return elements.filter((e) -> e.getParameterCount() <= validParametersCount).sorted(MEMBER_SCOPE_COMPARATOR)
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
	 * 使用反射查找无参的构造方法(包含未公开的构造方法)
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws UnsupportedException 不存在无参构造方法
	 */
	public static <T> T newInstance(Class<T> clazz) throws UnsupportedException {
		Constructor<T> constructor = getDeclaredConstructor(clazz);
		if (constructor == null) {
			throw new UnsupportedException(clazz.getName());
		}

		try {
			return constructor.newInstance();
		} catch (Exception e) {
			handleReflectionException(e);
		}
		throw new IllegalStateException("Should never get here");
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
	 * 使用空值构造实体
	 * 
	 * @param <T>
	 * @param entityClass
	 * @return
	 * @throws UnsupportedException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceWithNullValues(Class<T> entityClass) throws UnsupportedException {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Elements<Constructor<?>> elements = ReflectionUtils.getDeclaredConstructors(entityClass).convert((s) -> s
				.sorted(MEMBER_SCOPE_COMPARATOR).sorted(Comparator.comparingInt(Constructor::getParameterCount)));
		Iterator<Constructor<?>> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Constructor<?> constructor = iterator.next();
			return (T) newInstance(constructor, new Object[constructor.getParameterCount()]);
		}
		throw new UnsupportedException(entityClass.getName());
	}

	/**
	 * 根据参数来构造实体
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param params
	 * @return
	 * @throws UnsupportedException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceWithParams(Class<T> entityClass, Object... params) throws UnsupportedException {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Assert.requiredArgument(params != null, "params");
		Elements<ExecutableMatchingResults<Constructor<?>>> elements = matchParams(
				ReflectionUtils.getDeclaredConstructors(entityClass), false, params);
		Iterator<ExecutableMatchingResults<Constructor<?>>> iterator = elements.iterator();
		if (iterator.hasNext()) {
			ExecutableMatchingResults<Constructor<?>> results = iterator.next();
			try {
				return (T) ReflectionUtils.newInstance(results.getExecutable(), results.getParams());
			} catch (Exception e) {
				// 忽略
			}
		}
		throw new UnsupportedException(entityClass.getName());
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
}
