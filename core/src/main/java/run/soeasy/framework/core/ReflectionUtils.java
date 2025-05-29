package run.soeasy.framework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.ThrowingFunction;

public final class ReflectionUtils {
	public static final Constructor<?>[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor<?>[0];
	public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
	public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

	public static <T> ClassMembersLoader<T> find(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends T, ? extends ReflectiveOperationException> finder) {
		return search(clazz, (e) -> {
			T element = finder.apply(e);
			return element == null ? Elements.empty() : Elements.singleton(element);
		});
	}

	public static ClassMembersLoader<Constructor<?>> findConstructor(@NonNull Class<?> clazz,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getConstructor(parameterTypes));
	}

	public static ClassMembersLoader<Constructor<?>> findDeclaredConstructor(@NonNull Class<?> clazz,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getDeclaredConstructor(parameterTypes));
	}

	public static ClassMembersLoader<Field> findDeclaredField(@NonNull Class<?> clazz, @NonNull String name) {
		return find(clazz, (e) -> e.getDeclaredField(name));
	}

	public static ClassMembersLoader<Method> findDeclaredMethod(@NonNull Class<?> clazz, @NonNull String name,
			Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getDeclaredMethod(name, parameterTypes));
	}

	public static ClassMembersLoader<Field> findField(@NonNull Class<?> clazz, @NonNull String name) {
		return find(clazz, (e) -> e.getField(name));
	}

	public static ClassMembersLoader<Method> findMethod(@NonNull Class<?> clazz, @NonNull String name,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getMethod(name, parameterTypes));
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

	public static Method getCloneMethod(Cloneable source) {
		if (source == null) {
			return null;
		}

		Method method = findMethod(source.getClass(), "clone").withSuperclass().first();
		if (method == null) {
			return null;
		}

		if (ClassUtils.isAssignableValue(method.getReturnType(), source)) {
			return method;
		}
		return null;
	}

	/**
	 * @see Class#getConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Constructor<?>> getConstructors(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getConstructors);
	}

	/**
	 * @see Class#getDeclaredConstructors()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Constructor<?>> getDeclaredConstructors(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredConstructors);
	}

	/**
	 * @see Class#getDeclaredFields()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Field> getDeclaredFields(@NonNull Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredFields);
	}

	/**
	 * @see Class#getDeclaredMethods()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Method> getDeclaredMethods(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredMethods);
	}

	/**
	 * @see Class#getFields()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Field> getFields(@NonNull Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getFields);
	}

	public static <T> ClassMembersLoader<T> getMembers(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends T[], ? extends ReflectiveOperationException> loader) {
		return search(clazz, (e) -> {
			T[] array = loader.apply(e);
			return ArrayUtils.isEmpty(array) ? Elements.empty() : Elements.forArray(array);
		});
	}

	/**
	 * @see Class#getMethods()
	 * @param sourceClass
	 * @return
	 */
	public static ClassMembersLoader<Method> getMethods(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getMethods);
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
		if (ex instanceof ReflectiveOperationException) {
			throw new IllegalStateException("Reflective operation: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access: " + ex.getMessage());
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
	public static Object invoke(Method method, Object target, Object... args) {
		makeAccessible(method);
		try {
			return method.invoke(target, args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
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
		return (method != null && method.getName().equals("hashCode") && method.getParameterCount() == 0);
	}

	/**
	 * Determine whether the given method is originally declared by
	 * {@link java.lang.Object}.
	 */
	public static boolean isObjectMethod(Method method) {
		return !findMethod(Object.class, method.getName(), method.getParameterTypes()).isEmpty();
	}

	/**
	 * Determine whether the given method is a "toString" method.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterCount() == 0);
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

	public static <T> T newInstance(@NonNull Constructor<T> constructor, @NonNull Object... args) {
		makeAccessible(constructor);
		try {
			return constructor.newInstance(args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
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

	public static <T> ClassMembersLoader<T> search(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends Elements<T>, ? extends ReflectiveOperationException> searcher) {
		return new ClassMembersLoader<>(clazz, (c) -> {
			try {
				return searcher.apply(c);
			} catch (ReflectiveOperationException | SecurityException e) {
				return Elements.empty();
			}
		});
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
}
