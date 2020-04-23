/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.core.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.cglib.core.ReflectUtils;
import scw.core.Assert;
import scw.core.Verification;
import scw.core.annotation.Order;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.lang.Ignore;
import scw.util.FormatUtils;
import scw.util.comparator.CompareUtils;
import scw.util.value.Value;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>
 * Only intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2
 */
public abstract class ReflectionUtils {
	/**
	 * 此方法不是用classloader来判断的，这是以反射的方式来判断此类是否完全可用,如果要判断一个类是否存在应该使用ClassUtils的方法
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isPresent(Class<?> clazz) {
		try {
			clazz.getDeclaredConstructors();
			clazz.getDeclaredFields();
			clazz.getDeclaredMethods();
		} catch (NoClassDefFoundError e) {
			return false;
		}
		return true;
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name}. Searches all superclasses up to {@link Object}
	 * .
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name} and/or {@link Class type}. Searches all
	 * superclasses up to {@link Object}.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field (may be {@code null} if type is
	 *            specified)
	 * @param type
	 *            the type of the field (may be {@code null} if name is
	 *            specified)
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Set the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object} to the specified {@code value}
	 * . In accordance with {@link Field#set(Object, Object)} semantics, the new
	 * value is automatically unwrapped if the underlying field has a primitive
	 * type.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field
	 *            the field to set
	 * @param target
	 *            the target object on which to set the field
	 * @param value
	 *            the value to set; may be {@code null}
	 */
	public static void setField(Field field, Object target, Object value) {
		try {
			field.set(target, value);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Get the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object}. In accordance with
	 * {@link Field#get(Object)} semantics, the returned value is automatically
	 * wrapped if the underlying field has a primitive type.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field
	 *            the field to get
	 * @param target
	 *            the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object getField(Field field, Object target) {
		try {
			return field.get(target);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and no parameters. Searches all superclasses up to {@code Object}.
	 * <p>
	 * Returns {@code null} if no {@link Method} can be found.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the method
	 * @return the Method object, or {@code null} if none found
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class[0]);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and parameter types. Searches all superclasses up to {@code Object}.
	 * <p>
	 * Returns {@code null} if no {@link Method} can be found.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the method
	 * @param paramTypes
	 *            the parameter types of the method (may be {@code null} to
	 *            indicate any signature)
	 * @return the Method object, or {@code null} if none found
	 */
	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				if (name.equals(method.getName())
						&& (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					setAccessibleMethod(method);
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static Method findMethod(Class<?> clazz, String name, Object... args) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			for (Method method : (searchType.isInterface() ? searchType.getMethods()
					: searchType.getDeclaredMethods())) {
				if (!method.getName().equals(name)) {
					continue;
				}

				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length != args.length) {
					continue;
				}

				boolean b = true;
				for (int i = 0; i < parameterTypes.length; i++) {
					if (!ClassUtils.isAssignableValue(parameterTypes[i], args[i])) {
						b = false;
						break;
					}
				}

				if (b) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with no arguments. The target object can be {@code null} when invoking a
	 * static {@link Method}.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException}.
	 * 
	 * @param method
	 *            the method to invoke
	 * @param target
	 *            the target object to invoke the method on
	 * @return the invocation result, if any
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with the supplied arguments. The target object can be {@code null} when
	 * invoking a static {@link Method}.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException}.
	 * 
	 * @param method
	 *            the method to invoke
	 * @param target
	 *            the target object to invoke the method on
	 * @param args
	 *            the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 */
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target
	 * object with no arguments.
	 * 
	 * @param method
	 *            the method to invoke
	 * @param target
	 *            the target object to invoke the method on
	 * @return the invocation result, if any
	 * @throws SQLException
	 *             the JDBC API SQLException to rethrow (if any)
	 * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
		return invokeJdbcMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target
	 * object with the supplied arguments.
	 * 
	 * @param method
	 *            the method to invoke
	 * @param target
	 *            the target object to invoke the method on
	 * @param args
	 *            the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 * @throws SQLException
	 *             the JDBC API SQLException to rethrow (if any)
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof SQLException) {
				throw (SQLException) ex.getTargetException();
			}
			handleInvocationTargetException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Handle the given reflection exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message else.
	 * 
	 * @param ex
	 *            the reflection exception to handle
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

	/**
	 * Handle the given invocation target exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of such a root
	 * cause. Throws an IllegalStateException else.
	 * 
	 * @param ex
	 *            the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}. Should
	 * only be called if no checked exception is expected to be thrown by the
	 * target method.
	 * <p>
	 * Rethrows the underlying exception cast to an {@link RuntimeException} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param ex
	 *            the exception to rethrow
	 * @throws RuntimeException
	 *             the rethrown exception
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
	 * only be called if no checked exception is expected to be thrown by the
	 * target method.
	 * <p>
	 * Rethrows the underlying exception cast to an {@link Exception} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param ex
	 *            the exception to rethrow
	 * @throws Exception
	 *             the rethrown exception (in case of a checked exception)
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
	 * Determine whether the given method explicitly declares the given
	 * exception or one of its superclasses, which means that an exception of
	 * that type can be propagated as-is within a reflective invocation.
	 * 
	 * @param method
	 *            the declaring method
	 * @param exceptionType
	 *            the exception to throw
	 * @return {@code true} if the exception can be thrown as-is; {@code false}
	 *         if it needs to be wrapped
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
	 * Determine whether the given field is a "public static final" constant.
	 * 
	 * @param field
	 *            the field to check
	 */
	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
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
		try {
			Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (SecurityException ex) {
			return false;
		} catch (NoSuchMethodException ex) {
			return false;
		}
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * 
	 * @param field
	 *            the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * 
	 * @param method
	 *            the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible
	 * if necessary. The {@code setAccessible(true)} method is only called when
	 * actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * 
	 * @param ctor
	 *            the constructor to make accessible
	 * @see java.lang.reflect.Constructor#setAccessible
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses.
	 * <p>
	 * The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by a {@link MethodFilter}.
	 * 
	 * @param clazz
	 *            class to start looking at
	 * @param mc
	 *            the callback to invoke for each method
	 * @see #doWithMethods(Class, MethodCallback, MethodFilter)
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc) throws IllegalArgumentException {
		doWithMethods(clazz, mc, null);
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses (or given interface and super-interfaces).
	 * <p>
	 * The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by the specified {@link MethodFilter}.
	 * 
	 * @param clazz
	 *            class to start looking at
	 * @param mc
	 *            the callback to invoke for each method
	 * @param mf
	 *            the filter that determines the methods to apply the callback
	 *            to
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf)
			throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException(
						"Shouldn't be illegal to access method '" + method.getName() + "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		} else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}

	/**
	 * Get all declared methods on the leaf class and all superclasses. Leaf
	 * class methods are included first.
	 */
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
		final List<Method> methods = new ArrayList<Method>(32);
		doWithMethods(leafClass, new MethodCallback() {
			public void doWith(Method method) {
				methods.add(method);
			}
		});
		return methods.toArray(new Method[methods.size()]);
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * 
	 * @param clazz
	 *            the target class to analyze
	 * @param fc
	 *            the callback to invoke for each field
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc) throws IllegalArgumentException {
		doWithFields(clazz, fc, null);
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * 
	 * @param clazz
	 *            the target class to analyze
	 * @param fc
	 *            the callback to invoke for each field
	 * @param ff
	 *            the filter that determines the fields to apply the callback to
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		Class<?> targetClass = clazz;
		do {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				// Skip static and final fields.
				if (ff != null && !ff.matches(field)) {
					continue;
				}
				try {
					fc.doWith(field);
				} catch (IllegalAccessException ex) {
					throw new IllegalStateException(
							"Shouldn't be illegal to access field '" + field.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);
	}

	/**
	 * Given the source object and the destination, which must be the same class
	 * or a subclass, copy all fields, including inherited fields. Designed to
	 * work on objects with public no-arg constructors.
	 * 
	 * @throws IllegalArgumentException
	 *             if the arguments are incompatible
	 */
	public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
		if (src == null) {
			throw new IllegalArgumentException("Source for field copy cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("Destination for field copy cannot be null");
		}
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName()
					+ "] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				makeAccessible(field);
				Object srcValue = field.get(src);
				field.set(dest, srcValue);
			}
		}, COPYABLE_FIELDS);
	}

	/**
	 * Action to take on each method.
	 */
	public interface MethodCallback {

		/**
		 * Perform an operation using the given method.
		 * 
		 * @param method
		 *            the method to operate on
		 */
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter methods to be operated on by a method
	 * callback.
	 */
	public interface MethodFilter {

		/**
		 * Determine whether the given method matches.
		 * 
		 * @param method
		 *            the method to check
		 */
		boolean matches(Method method);
	}

	/**
	 * Callback interface invoked on each field in the hierarchy.
	 */
	public interface FieldCallback {

		/**
		 * Perform an operation using the given field.
		 * 
		 * @param field
		 *            the field to operate on
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter fields to be operated on by a field
	 * callback.
	 */
	public interface FieldFilter {

		/**
		 * Determine whether the given field matches.
		 * 
		 * @param field
		 *            the field to check
		 */
		boolean matches(Field field);
	}

	/**
	 * Pre-built FieldFilter that matches all non-static, non-final fields.
	 */
	public static FieldFilter COPYABLE_FIELDS = new FieldFilter() {

		public boolean matches(Field field) {
			return !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()));
		}
	};

	/**
	 * Pre-built MethodFilter that matches all non-bridge methods.
	 */
	public static MethodFilter NON_BRIDGED_METHODS = new MethodFilter() {

		public boolean matches(Method method) {
			return !method.isBridge();
		}
	};

	/**
	 * Pre-built MethodFilter that matches all non-bridge methods which are not
	 * declared on {@code java.lang.Object}.
	 */
	public static MethodFilter USER_DECLARED_METHODS = new MethodFilter() {

		public boolean matches(Method method) {
			return (!method.isBridge() && method.getDeclaringClass() != Object.class);
		}
	};

	public static void loadMethod(Object bean, String propertyPrefix, PropertyFactory propertyFactory,
			final InstanceFactory instanceFactory, Set<String> ignoreNames) {
		loadMethod(bean, Arrays.asList("set", "add"), propertyPrefix, propertyFactory, instanceFactory, ignoreNames,
				null);
	}

	public static void loadMethod(Object bean, Collection<String> methodPrefixs, String propertyPrefix,
			PropertyFactory propertyFactory, final InstanceFactory instanceFactory, final Set<String> ignoreName,
			final Verification<Type> beanVerification) {
		loadMethod(bean, methodPrefixs, propertyPrefix, propertyFactory, new PropertyMapper<Value>() {

			public Object mapper(String name, Value value, Type type) throws Exception {
				if (StringUtils.isEmpty(value.getAsString())) {
					return null;
				}

				if (ignoreName != null && ignoreName.contains(name)) {
					return null;
				}

				if (ValueUtils.isCommonType(type)) {
					return value.getAsObject(type);
				}

				if (TypeUtils.isInterface(type) || TypeUtils.isAbstract(type)) {
					String className = TypeUtils.getClassName(type);
					return instanceFactory.isInstance(className) ? instanceFactory.getInstance(className) : null;
				}

				if (beanVerification == null) {
					return value.getAsObject(type);
				}

				if (beanVerification.verification(type)) {
					String className = TypeUtils.getClassName(type);
					return instanceFactory.isInstance(className) ? instanceFactory.getInstance(className) : null;
				} else {
					return value.getAsObject(type);
				}
			}
		});
	}

	public static void loadMethod(Object bean, Collection<String> methodPrefixs, String propertyPrefix,
			PropertyFactory propertyFactory, PropertyMapper<Value> propertyMapper) {
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
					Value value = propertyFactory.get(key);
					if (value == null) {
						continue;
					}
					Object v;
					try {
						v = propertyMapper.mapper(name, value, types[0]);
						if (v != null) {
							ReflectionUtils.setAccessibleMethod(method);
							method.invoke(bean, v);
						}
					} catch (Exception e) {
						FormatUtils.warn(ReflectionUtils.class, "向对象{}，插入name={},value={}时异常",
								bean.getClass().getName(), name, value);
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
					FormatUtils.warn(ReflectUtils.class, "向对象{}，插入name={},value={}时异常", type.getName(), entry.getKey(),
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
					FormatUtils.warn(ReflectUtils.class, "向对象{}，插入name={},value={}时异常(调用set方法)", type.getName(),
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
		return getConstructor(ClassUtils.forName(className), isPublic, parameterTypes);
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, boolean isPublic, String... parameterTypeNames)
			throws ClassNotFoundException {
		return getConstructor(type, isPublic,
				ClassUtils.forNames(ClassUtils.getDefaultClassLoader(), parameterTypeNames));
	}

	public static Constructor<?> getConstructor(String className, boolean isPublic, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException {
		return getConstructor(ClassUtils.forName(className), isPublic,
				ClassUtils.forName(className, ClassUtils.getDefaultClassLoader()));
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
	public static <T> Object invoke(Class<T> type, Object instance, String name, Map<String, Object> parameterMap)
			throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return getMethod(type, name).invoke(instance);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		for (Method method : type.getDeclaredMethods()) {
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

	public static Method getMethod(String className, String methodName, Class<?>... parameterTypes) {
		Class<?> clz = ClassUtils.forNameNullable(className);
		if (clz == null) {
			return null;
		}

		return getMethod(clz, methodName, parameterTypes);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
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
						FormatUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
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
						FormatUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
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

	public static Method getGetterMethod(Class<?> clazz, Field field) {
		Method getter = null;
		Class<?> clz = clazz;
		if (TypeUtils.isBoolean(field.getType())) {
			String methodNameSuffix = field.getName();
			if (methodNameSuffix.startsWith("is")) {
				FormatUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
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
			if (Modifier.isPrivate(getter.getModifiers())) {
				return null;
			}

			getter.setAccessible(true);
		}

		return getter;
	}

	public static Method getSetterMethod(Class<?> clazz, Field field) {
		Method setter = null;
		Class<?> clz = clazz;
		if (TypeUtils.isBoolean(field.getType())) {
			String methodNameSuffix = field.getName();
			if (methodNameSuffix.startsWith("is")) {
				FormatUtils.warn(ReflectUtils.class, "Boolean类型的字段不应该以is开头,class:{},field:{}", clz.getName(),
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
				setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(field.getName(), 0, 1), field.getType());
			} catch (NoSuchMethodException e) {
			}
		}

		if (setter != null) {
			if (Modifier.isPrivate(setter.getModifiers())) {
				return null;
			}
			setter.setAccessible(true);
		}
		return setter;
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

	public static void setAccessibleConstructor(Constructor<?> constructor) {
		if (!constructor.isAccessible() && (Modifier.isPrivate(constructor.getModifiers())
				|| Modifier.isProtected(constructor.getModifiers()))) {
			constructor.setAccessible(true);
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
		Method method = getSetterMethod(clz, field);
		try {
			if (method == null) {
				setAccessibleField(field);
				field.set(obj, value);
				return null;
			} else {
				return method.invoke(obj, value);
			}
		} catch (Exception e) {
			FormatUtils.warn(ReflectUtils.class, "向对象{}，插入field={}时异常", clz.getName(), field.getName());
			throw e;
		}
	}

	public static Object getFieldValue(Class<?> clz, Object obj, Field field) throws Exception {
		Method method = getGetterMethod(clz, field);
		try {
			if (method == null) {
				setAccessibleField(field);
				return field.get(obj);
			} else {
				return method.invoke(obj);
			}
		} catch (Exception e) {
			FormatUtils.warn(ReflectUtils.class, "获取对象{}中field={}时值时异常", clz.getName(), field.getName());
			throw e;
		}
	}

	public static Object setFieldValueAutoType(Class<?> clz, Field field, Object obj, String value) throws Exception {
		return setFieldValue(clz, field, obj, ValueUtils.parse(value, field.getGenericType()));
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
		return invokeStaticMethod(ClassUtils.forName(className), name, parameterTypes, params);
	}

	public static <T> Collection<Constructor<?>> getConstructorOrderList(Class<?> clazz) {
		LinkedList<Constructor<?>> autoList = new LinkedList<Constructor<?>>();
		LinkedList<Constructor<?>> defList = new LinkedList<Constructor<?>>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getAnnotation(Ignore.class) != null) {
				continue;
			}

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
				Deprecated d1 = o1.getAnnotation(Deprecated.class);
				Deprecated d2 = o2.getAnnotation(Deprecated.class);

				int v1 = o1.getParameterTypes().length;
				int v2 = o2.getParameterTypes().length;
				if (!(d1 != null && d2 != null)) {
					if (d1 != null) {
						v1 = -1;
					}

					if (d2 != null) {
						v2 = -1;
					}
				}

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
