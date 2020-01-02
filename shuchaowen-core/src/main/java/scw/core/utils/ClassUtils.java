package scw.core.utils;

import java.beans.Introspector;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.core.Assert;
import scw.core.Constants;
import scw.core.cglib.core.TypeUtils;

public final class ClassUtils {
	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	private static final String TYPE_CLASS_PREFIX = "class ";
	private static final String TYPE_INTERFACE_PREFIX = "interface ";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * Map with primitive wrapper type as key and corresponding primitive type
	 * as value, for example: Integer.class -> int.class.
	 */
	private static final IdentityHashMap<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(
			8);

	/**
	 * Map with primitive type as key and corresponding wrapper type as value,
	 * for example: int.class -> Integer.class.
	 */
	private static final IdentityHashMap<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(
			8);

	/**
	 * Map with primitive type name as key and corresponding primitive type as
	 * value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

	/**
	 * Map with common "java.lang" class name as key and corresponding Class as
	 * value. Primarily for efficient deserialization of remote invocations.
	 */
	private static final Map<String, Class<?>> commonClassCache = new HashMap<String, Class<?>>(32);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			registerCommonClasses(entry.getKey());
		}

		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(Arrays.asList(new Class<?>[] { boolean[].class, byte[].class, char[].class,
				double[].class, float[].class, int[].class, long[].class, short[].class }));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}

		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class,
				Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Object.class, Object[].class,
				Class.class, Class[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class,
				StackTraceElement.class, StackTraceElement[].class);
	}

	private ClassUtils() {
	};

	/**
	 * 获取父类 不包含java.lang.Object
	 * 
	 * @param clz
	 * @return
	 */
	public static Class<?> getSuperClass(Class<?> clz) {
		Class<?> superClz = clz.getSuperclass();
		if (superClz == null || Object.class.getName().equals(superClz.getName())) {
			return null;
		}
		return superClz;
	}

	/**
	 * 获取所有父类 不包含java.lang.Object
	 * 
	 * @param clz
	 * @return
	 */
	public static List<Class<?>> getSuperClassList(Class<?> clz) {
		List<Class<?>> clzList = new ArrayList<Class<?>>();
		Class<?> superClz = getSuperClass(clz);
		while (superClz != null) {
			clzList.add(superClz);
			superClz = getSuperClass(superClz);
		}
		return clzList;
	}

	public static String getProxyRealClassName(Class<?> clz) {
		return getProxyRealClassName(clz.getName());
	}

	public static String getProxyRealClassName(String cglibName) {
		int index = cglibName.indexOf(CGLIB_CLASS_SEPARATOR);
		if (index == -1) {
			return cglibName;
		} else {
			return cglibName.substring(0, index);
		}
	}

	/**
	 * Check whether the given object is a CGLIB proxy.
	 * 
	 * @param object
	 *            the object to check
	 * @see shuchaowen.spring.aop.support.AopUtils#isCglibProxy(Object)
	 */
	public static boolean isCglibProxy(Object object) {
		return ClassUtils.isCglibProxyClass(object.getClass());
	}

	/**
	 * Check whether the specified class is a CGLIB-generated class.
	 * 
	 * @param clazz
	 *            the class to check
	 */
	public static boolean isCglibProxyClass(Class<?> clazz) {
		return (clazz != null && isCglibProxyClassName(clazz.getName()));
	}

	/**
	 * Check whether the specified class name is a CGLIB-generated class.
	 * 
	 * @param className
	 *            the class name to check
	 */
	public static boolean isCglibProxyClassName(String className) {
		return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
	}

	/**
	 * Register the given common classes with the ClassUtils cache.
	 */
	private static void registerCommonClasses(Class<?>... commonClasses) {
		for (Class<?> clazz : commonClasses) {
			commonClassCache.put(clazz.getName(), clazz);
		}
	}

	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>
	 * Call this method if you intend to use the thread context ClassLoader in a
	 * scenario where you absolutely need a non-null ClassLoader reference: for
	 * example, for class path resource loading (but not necessarily for
	 * {@code Class.forName}, which accepts a {@code null} ClassLoader reference
	 * as well).
	 * 
	 * @return the default ClassLoader (never {@code null})
	 * @see Thread#getContextClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}

		if (cl == null) {
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}

	/**
	 * Override the thread context ClassLoader with the environment's bean
	 * ClassLoader if necessary, i.e. if the bean ClassLoader is not equivalent
	 * to the thread context ClassLoader already.
	 * 
	 * @param classLoaderToUse
	 *            the actual ClassLoader to use for the thread context
	 * @return the original thread context ClassLoader, or {@code null} if not
	 *         overridden
	 */
	public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(classLoaderToUse);
			return threadContextClassLoader;
		} else {
			return null;
		}
	}

	public static Class<?>[] forNames(String... classNames) throws ClassNotFoundException {
		return forNames(getDefaultClassLoader(), classNames);
	}

	public static Class<?>[] forNames(ClassLoader classLoader, String... className) throws ClassNotFoundException {
		Class<?>[] types = new Class<?>[className.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = forName(className[i], classLoader);
		}
		return types;
	}

	public static Class<?> forName(String name) throws ClassNotFoundException {
		return forName(name, getDefaultClassLoader());
	}

	public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
		return forName(name, false, classLoader);
	}

	public static Class<?> forName(String name, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		Assert.notNull(name, "Name must not be null");

		Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz == null) {
			clazz = commonClassCache.get(name);
		}
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, initialize, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
			String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, initialize, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[[I" or "[[Ljava.lang.String;" style arrays
		if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
			String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
			Class<?> elementClass = forName(elementName, initialize, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		if (name.startsWith(TYPE_CLASS_PREFIX)) {
			return forName(name.substring(TYPE_CLASS_PREFIX.length()), initialize, classLoader);
		}

		if (name.startsWith(TYPE_INTERFACE_PREFIX)) {
			return forName(name.substring(TYPE_INTERFACE_PREFIX.length()), initialize, classLoader);
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return forName0(name, initialize, classLoaderToUse);
		} catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
				// try {
				return forName0(innerClassName, initialize, classLoaderToUse);
				// } catch (ClassNotFoundException ex2) {
				// swallow - let original exception get through
				// }
			}
			throw ex;
		}
	}

	private static Class<?> forName0(String name, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		if (initialize) {
			return Class.forName(name, true, classLoader);
		} else {
			return classLoader.loadClass(name);
		}
	}

	/**
	 * Resolve the given class name into a Class instance. Supports primitives
	 * (like "int") and array class names (like "String[]").
	 * <p>
	 * This is effectively equivalent to the {@code forName} method with the
	 * same arguments, with the only difference being the exceptions thrown in
	 * case of class loading failure.
	 * 
	 * @param className
	 *            the name of the Class
	 * @param classLoader
	 *            the class loader to use (may be {@code null}, which indicates
	 *            the default class loader)
	 * @return Class instance for the supplied name
	 * @throws IllegalArgumentException
	 *             if the class name was not resolvable (that is, the class
	 *             could not be found or the class file could not be loaded)
	 * @see #forName(String, ClassLoader)
	 */
	public static Class<?> resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException {
		try {
			return forName(className, classLoader);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Cannot find class [" + className + "]", ex);
		} catch (LinkageError ex) {
			throw new IllegalArgumentException(
					"Error loading class [" + className + "]: problem with class file or dependent class.", ex);
		}
	}

	/**
	 * Resolve the given class name as primitive class, if appropriate,
	 * according to the JVM's naming rules for primitive classes.
	 * <p>
	 * Also supports the JVM's internal class names for primitive arrays. Does
	 * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
	 * only supported by {@link #forName(String, ClassLoader)}.
	 * 
	 * @param name
	 *            the name of the potentially primitive class
	 * @return the primitive class, or {@code null} if the name does not denote
	 *         a primitive class or primitive array class
	 */
	public static Class<?> resolvePrimitiveClassName(String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	/**
	 * 使用当前线程的加载器来判断是否存在此类
	 * 
	 * @param className
	 * @return
	 */
	public static boolean isPresent(String className) {
		return isPresent(className, getDefaultClassLoader());
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is
	 * present and can be loaded. Will return {@code false} if either the class
	 * or one of its dependencies is not present or cannot be loaded.
	 * 
	 * @param className
	 *            the name of the class to check
	 * @param classLoader
	 *            the class loader to use (may be {@code null}, which indicates
	 *            the default class loader)
	 * @return whether the specified class is present
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		try {
			forName(className, true, classLoader);
			return true;
		} catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return false;
		}
	}

	/**
	 * Return the user-defined class for the given instance: usually simply the
	 * class of the given instance, but the original class in case of a
	 * CGLIB-generated subclass.
	 * 
	 * @param instance
	 *            the instance to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getUserClass(instance.getClass());
	}

	/**
	 * Return the user-defined class for the given class: usually simply the
	 * given class, but the original class in case of a CGLIB-generated
	 * subclass.
	 * 
	 * @param clazz
	 *            the class to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Class<?> clazz) {
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	/**
	 * Check whether the given class is cache-safe in the given context, i.e.
	 * whether it is loaded by the given ClassLoader or a parent of it.
	 * 
	 * @param clazz
	 *            the class to analyze
	 * @param classLoader
	 *            the ClassLoader to potentially cache metadata in
	 */
	public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		ClassLoader target = clazz.getClassLoader();
		if (target == null) {
			return false;
		}
		ClassLoader cur = classLoader;
		if (cur == target) {
			return true;
		}
		while (cur != null) {
			cur = cur.getParent();
			if (cur == target) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the class name without the qualified package name.
	 * 
	 * @param className
	 *            the className to get the short name for
	 * @return the class name of the class without the package name
	 * @throws IllegalArgumentException
	 *             if the className is empty
	 */
	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	/**
	 * Get the class name without the qualified package name.
	 * 
	 * @param clazz
	 *            the class to get the short name for
	 * @return the class name of the class without the package name
	 */
	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	/**
	 * Return the short string name of a Java class in uncapitalized JavaBeans
	 * property format. Strips the outer class name in case of an inner class.
	 * 
	 * @param clazz
	 *            the class
	 * @return the short name rendered in a standard JavaBeans property format
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	public static String getShortNameAsProperty(Class<?> clazz) {
		String shortName = ClassUtils.getShortName(clazz);
		int dotIndex = shortName.lastIndexOf('.');
		shortName = (dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
		return Introspector.decapitalize(shortName);
	}

	/**
	 * Determine the name of the class file, relative to the containing package:
	 * e.g. "String.class"
	 * 
	 * @param clazz
	 *            the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	/**
	 * Determine the name of the package of the given class, e.g. "java.lang"
	 * for the {@code java.lang.String} class.
	 * 
	 * @param clazz
	 *            the class
	 * @return the package name, or the empty String if the class is defined in
	 *         the default package
	 */
	public static String getPackageName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return getPackageName(clazz.getName());
	}

	/**
	 * Determine the name of the package of the given fully-qualified class
	 * name, e.g. "java.lang" for the {@code java.lang.String} class name.
	 * 
	 * @param fqClassName
	 *            the fully-qualified class name
	 * @return the package name, or the empty String if the class is defined in
	 *         the default package
	 */
	public static String getPackageName(String fqClassName) {
		Assert.notNull(fqClassName, "Class name must not be null");
		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
	}

	/**
	 * Return the qualified name of the given class: usually simply the class
	 * name, but component type class name + "[]" for arrays.
	 * 
	 * @param clazz
	 *            the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Build a nice qualified name for an array: component type class name +
	 * "[]".
	 * 
	 * @param clazz
	 *            the array class
	 * @return a qualified name for the array class
	 */
	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append(ClassUtils.ARRAY_SUFFIX);
		}
		result.insert(0, clazz.getName());
		return result.toString();
	}

	/**
	 * Return a descriptive name for the given object's type: usually simply the
	 * class name, but component type class name + "[]" for arrays, and an
	 * appended list of implemented interfaces for JDK proxies.
	 * 
	 * @param value
	 *            the value to introspect
	 * @return the qualified name of the class
	 */
	public static String getDescriptiveType(Object value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (Proxy.isProxyClass(clazz)) {
			StringBuilder result = new StringBuilder(clazz.getName());
			result.append(" implementing ");
			Class<?>[] ifcs = clazz.getInterfaces();
			for (int i = 0; i < ifcs.length; i++) {
				result.append(ifcs[i].getName());
				if (i < ifcs.length - 1) {
					result.append(',');
				}
			}
			return result.toString();
		} else if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Check whether the given class matches the user-specified type name.
	 * 
	 * @param clazz
	 *            the class to check
	 * @param typeName
	 *            the type name to match
	 */
	public static boolean matchesTypeName(Class<?> clazz, String typeName) {
		return (typeName != null && (typeName.equals(clazz.getName()) || typeName.equals(clazz.getSimpleName())
				|| (clazz.isArray() && typeName.equals(getQualifiedNameForArray(clazz)))));
	}

	/**
	 * Check if the given class represents a primitive wrapper, i.e. Boolean,
	 * Byte, Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param clazz
	 *            the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return primitiveWrapperTypeMap.containsKey(clazz);
	}

	/**
	 * Check if the given class represents a primitive (i.e. boolean, byte,
	 * char, short, int, long, float, or double) or a primitive wrapper (i.e.
	 * Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
	 * 
	 * @param clazz
	 *            the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * Check if the given class represents an array of primitives, i.e. boolean,
	 * byte, char, short, int, long, float, or double.
	 * 
	 * @param clazz
	 *            the class to check
	 * @return whether the given class is a primitive array class
	 */
	public static boolean isPrimitiveArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * Check if the given class represents an array of primitive wrappers, i.e.
	 * Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param clazz
	 *            the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	/**
	 * Resolve the given class if it is a primitive class, returning the
	 * corresponding primitive wrapper type instead.
	 * 
	 * @param clazz
	 *            the class to check
	 * @return the original class, or a primitive wrapper for the original
	 *         primitive type
	 */
	public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
	}

	/**
	 * Check if the right-hand side type may be assigned to the left-hand side
	 * type, assuming setting by reflection. Considers primitive wrapper classes
	 * as assignable to the corresponding primitive types.
	 * 
	 * @param lhsType
	 *            the target type
	 * @param rhsType
	 *            the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 * @see TypeUtils#isAssignable
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		Assert.notNull(lhsType, "Left-hand side type must not be null");
		Assert.notNull(rhsType, "Right-hand side type must not be null");
		if (lhsType.isAssignableFrom(rhsType)) {
			return true;
		}
		if (lhsType.isPrimitive()) {
			Class resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
			if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
				return true;
			}
		} else {
			Class resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
			if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAssignable(Collection<Class<?>> lhsTypes, Collection<Class<?>> rhsTypes) {
		if (CollectionUtils.isEmpty(lhsTypes)) {
			return CollectionUtils.isEmpty(rhsTypes);
		}

		if (lhsTypes.size() != (CollectionUtils.isEmpty(rhsTypes) ? 0 : rhsTypes.size())) {
			return false;
		}

		Iterator<Class<?>> lhsIterator = lhsTypes.iterator();
		Iterator<Class<?>> rhsIterator = rhsTypes.iterator();
		while (lhsIterator.hasNext() && rhsIterator.hasNext()) {
			if (!isAssignable(lhsIterator.next(), rhsIterator.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the given type is assignable from the given value, assuming
	 * setting by reflection. Considers primitive wrapper classes as assignable
	 * to the corresponding primitive types.
	 * 
	 * @param type
	 *            the target type
	 * @param value
	 *            the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(Class<?> type, Object value) {
		Assert.notNull(type, "Type must not be null");
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	public static boolean isAssignableValue(Collection<Class<?>> types, Collection<Object> values) {
		if (CollectionUtils.isEmpty(types)) {
			return CollectionUtils.isEmpty(values);
		}

		if (types.size() != (CollectionUtils.isEmpty(values) ? 0 : values.size())) {
			return false;
		}

		Iterator<Class<?>> typeIterator = types.iterator();
		Iterator<Object> valueIterator = values.iterator();
		while (typeIterator.hasNext() && valueIterator.hasNext()) {
			if (!isAssignableValue(typeIterator.next(), valueIterator.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert a "/"-based resource path to a "."-based fully qualified class
	 * name.
	 * 
	 * @param resourcePath
	 *            the resource path pointing to a class
	 * @return the corresponding fully qualified class name
	 */
	public static String convertResourcePathToClassName(String resourcePath) {
		Assert.notNull(resourcePath, "Resource path must not be null");
		return resourcePath.replace('/', '.');
	}

	/**
	 * Convert a "."-based fully qualified class name to a "/"-based resource
	 * path.
	 * 
	 * @param className
	 *            the fully qualified class name
	 * @return the corresponding resource path, pointing to the class
	 */
	public static String convertClassNameToResourcePath(String className) {
		Assert.notNull(className, "Class name must not be null");
		return className.replace('.', '/');
	}

	/**
	 * Return a path suitable for use with {@code ClassLoader.getResource} (also
	 * suitable for use with {@code Class.getResource} by prepending a slash
	 * ('/') to the return value). Built by taking the package of the specified
	 * class file, converting all dots ('.') to slashes ('/'), adding a trailing
	 * slash if necessary, and concatenating the specified resource name to
	 * this. <br/>
	 * As such, this function may be used to build a path suitable for loading a
	 * resource file that is in the same package as a class file, although
	 * {@link shuchaowen.spring.core.io.ClassPathResource} is usually even more
	 * convenient.
	 * 
	 * @param clazz
	 *            the Class whose package will be used as the base
	 * @param resourceName
	 *            the resource name to append. A leading slash is optional.
	 * @return the built-up resource path
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
		Assert.notNull(resourceName, "Resource name must not be null");
		if (!resourceName.startsWith("/")) {
			return classPackageAsResourcePath(clazz) + "/" + resourceName;
		}
		return classPackageAsResourcePath(clazz) + resourceName;
	}

	/**
	 * Given an input class object, return a string which consists of the
	 * class's package name as a pathname, i.e., all dots ('.') are replaced by
	 * slashes ('/'). Neither a leading nor trailing slash is added. The result
	 * could be concatenated with a slash and the name of a resource and fed
	 * directly to {@code ClassLoader.getResource()}. For it to be fed to
	 * {@code Class.getResource} instead, a leading slash would also have to be
	 * prepended to the returned value.
	 * 
	 * @param clazz
	 *            the input class. A {@code null} value or the default (empty)
	 *            package will result in an empty string ("") being returned.
	 * @return a path which represents the package name
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String classPackageAsResourcePath(Class<?> clazz) {
		if (clazz == null) {
			return "";
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf('.');
		if (packageEndIndex == -1) {
			return "";
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace('.', '/');
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces in
	 * the given array.
	 * <p>
	 * Basically like {@code AbstractCollection.toString()}, but stripping the
	 * "class "/"interface " prefix before every class name.
	 * 
	 * @param classes
	 *            a Collection of Class objects (may be {@code null})
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	@SuppressWarnings("rawtypes")
	public static String classNamesToString(Class... classes) {
		return classNamesToString(Arrays.asList(classes));
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces in
	 * the given collection.
	 * <p>
	 * Basically like {@code AbstractCollection.toString()}, but stripping the
	 * "class "/"interface " prefix before every class name.
	 * 
	 * @param classes
	 *            a Collection of Class objects (may be {@code null})
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	@SuppressWarnings("rawtypes")
	public static String classNamesToString(Collection<Class> classes) {
		if (CollectionUtils.isEmpty(classes)) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (Iterator<Class> it = classes.iterator(); it.hasNext();) {
			Class clazz = it.next();
			sb.append(clazz.getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Copy the given Collection into a Class array. The Collection must contain
	 * Class elements only.
	 * 
	 * @param collection
	 *            the Collection to copy
	 * @return the Class array ({@code null} if the passed-in Collection was
	 *         {@code null})
	 */
	public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new Class<?>[collection.size()]);
	}

	/**
	 * Return all interfaces that the given instance implements as array,
	 * including ones implemented by superclasses.
	 * 
	 * @param instance
	 *            the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as array
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] getAllInterfaces(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * Return all interfaces that the given class implements as array, including
	 * ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz
	 *            the class to analyze for interfaces
	 * @return all interfaces that the given object implements as array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	/**
	 * Return all interfaces that the given class implements as array, including
	 * ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz
	 *            the class to analyze for interfaces
	 * @param classLoader
	 *            the ClassLoader that the interfaces need to be visible in (may
	 *            be {@code null} when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as array
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
		Set<Class> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
		return ifcs.toArray(new Class[ifcs.size()]);
	}

	/**
	 * Return all interfaces that the given instance implements as Set,
	 * including ones implemented by superclasses.
	 * 
	 * @param instance
	 *            the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as Set
	 */
	@SuppressWarnings("rawtypes")
	public static Set<Class> getAllInterfacesAsSet(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	/**
	 * 获取一个类的所有接口 Return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz
	 *            the class to analyze for interfaces
	 * @return all interfaces that the given object implements as Set
	 */
	@SuppressWarnings("rawtypes")
	public static Set<Class> getAllInterfacesForClassAsSet(Class clazz) {
		return getAllInterfacesForClassAsSet(clazz, null);
	}

	/**
	 * 获取一个类的所有接口 Return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz
	 *            the class to analyze for interfaces
	 * @param classLoader
	 *            the ClassLoader that the interfaces need to be visible in (may
	 *            be {@code null} when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as Set
	 */
	@SuppressWarnings("rawtypes")
	public static Set<Class> getAllInterfacesForClassAsSet(Class clazz, ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface() && isVisible(clazz, classLoader)) {
			return Collections.singleton(clazz);
		}
		Set<Class> interfaces = new LinkedHashSet<Class>();
		while (clazz != null) {
			Class<?>[] ifcs = clazz.getInterfaces();
			for (Class<?> ifc : ifcs) {
				interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	/**
	 * 使用jdk代理获取代理类 Create a composite interface Class for the given interfaces,
	 * implementing the given interfaces in one single Class.
	 * <p>
	 * This implementation builds a JDK proxy class for the given interfaces.
	 * 
	 * @param interfaces
	 *            the interfaces to merge
	 * @param classLoader
	 *            the ClassLoader to create the composite Class in
	 * @return the merged interface as Class
	 * @see java.lang.reflect.Proxy#getProxyClass
	 */
	public static Class<?> createCompositeInterface(Class<?>[] interfaces, ClassLoader classLoader) {
		Assert.notEmpty(interfaces, "Interfaces must not be empty");
		Assert.notNull(classLoader, "ClassLoader must not be null");
		return Proxy.getProxyClass(classLoader, interfaces);
	}

	/**
	 * Check whether the given class is visible in the given ClassLoader.
	 * 
	 * @param clazz
	 *            the class to check (typically an interface)
	 * @param classLoader
	 *            the ClassLoader to check against (may be {@code null}, in
	 *            which case this method will always return {@code true})
	 */
	public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
		if (classLoader == null) {
			return true;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return false;
		}
	}

	public static boolean equals(Class<?>[] clazzArray1, Class<?>[] clazzArray2) {
		if (clazzArray1 == null || clazzArray1.length == 0) {
			return clazzArray2 == null || clazzArray2.length == 0;
		}

		if (clazzArray2 == null || clazzArray2.length == 0) {
			return clazzArray1 == null || clazzArray1.length == 0;
		}

		if (clazzArray1.length != clazzArray2.length) {
			return false;
		}

		for (int i = 0; i < clazzArray1.length; i++) {
			if (clazzArray1[i] != clazzArray2[i]) {
				return false;
			}
		}
		return true;
	}

	/************************************ 扫描类工具 *****************************************/

	/**
	 * 获取一个目录下的class
	 * 
	 * @param directorey
	 * @param prefix
	 * @return
	 */
	private static Collection<Class<?>> getClassesDirectoryList(ClassLoader classLoader) {
		LinkedHashSet<Class<?>> list = new LinkedHashSet<Class<?>>();
		String path = SystemPropertyUtils.getClassesDirectory();
		if (path == null) {
			return list;
		}
		appendDirectoryClass(null, new File(path), list, classLoader);
		return list;
	}

	private static void appendDirectoryClass(String rootPackage, File file, Collection<Class<?>> classList,
			ClassLoader classLoader) {
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				appendDirectoryClass(
						StringUtils.isEmpty(rootPackage) ? f.getName() + "." : rootPackage + f.getName() + ".", f,
						classList, classLoader);
			} else {
				if (f.getName().endsWith(".class")) {
					String classFile = StringUtils.isEmpty(rootPackage) ? f.getName() : rootPackage + f.getName();
					Class<?> clz = forFileName(classFile, classLoader);
					if (clz != null) {
						classList.add(clz);
					}
				}
			}
		}
	}

	/**
	 * 使用当前线程的类加载器
	 * 
	 * @param resource
	 * @return
	 */
	public static Collection<Class<?>> getClassList(String resource) {
		return getClassList(resource, getDefaultClassLoader());
	}

	public static Collection<Class<?>> getClassList(String resource, ClassLoader classLoader) {
		if (StringUtils.isEmpty(resource)) {
			return getClassesDirectoryList(classLoader);
		}

		String[] arr = StringUtils.commonSplit(resource);
		if (ArrayUtils.isEmpty(arr)) {
			return getClassesDirectoryList(classLoader);
		}

		LinkedHashSet<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (String pg : arr) {
			appendClassesByClassLoader(pg, classes, classLoader);
		}
		return classes;
	}

	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			Collection<Class<?>> classes, ClassLoader classLoader) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isDirectory()) || (file.getName().endsWith(CLASS_FILE_SUFFIX));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes,
						classLoader);
			} else {
				if (packageName.startsWith(".")) {
					packageName = packageName.substring(1);
				}
				Class<?> clazz = forFileName(packageName + "." + file.getName(), classLoader);
				if (clazz != null) {
					classes.add(clazz);
				}
			}
		}
	}

	private static Class<?> forFileName(String classFile, ClassLoader classLoader) {
		if (!classFile.endsWith(CLASS_FILE_SUFFIX)) {
			return null;
		}

		String name = classFile.substring(0, classFile.length() - 6);
		name = name.replaceAll("\\\\", ".");
		name = name.replaceAll("/", ".");
		try {
			return forName(name, classLoader);
		} catch (Throwable e) {
		}
		return null;
	}

	private static void appendClassesByURL(String packageName, String packageDirName, URL url,
			Collection<Class<?>> clazzList, ClassLoader classLoader) throws IOException {
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			String filePath = URLDecoder.decode(url.getFile(), Constants.DEFAULT_CHARSET_NAME);
			findAndAddClassesInPackageByFile(packageName, filePath, clazzList, classLoader);
		} else if ("jar".equals(protocol)) {
			JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}

				String name = entry.getName();
				if (name.charAt(0) == '/') {
					name = name.substring(1);
				}
				if (name.startsWith(packageDirName)) {
					Class<?> clazz = forFileName(name, classLoader);
					if (clazz != null) {
						clazzList.add(clazz);
					}
				}
			}
		}
	}

	private static void appendClassesByClassLoader(String packageName, Collection<Class<?>> clazzList,
			ClassLoader classLoader) {
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = classLoader.getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				appendClassesByURL(packageName, packageDirName, url, clazzList, classLoader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			dirs = ClassLoader.getSystemClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				appendClassesByURL(packageName, packageDirName, url, clazzList, classLoader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/************************************ 扫描类工具 *****************************************/

	public static Object[] cast(Class<?>[] types, Object[] args) {
		if (types == null || args == null) {
			throw new IllegalArgumentException("参数不能为空");
		}

		if (types.length != args.length) {
			throw new IllegalArgumentException("参数长度不一致");
		}

		if (types.length == 0) {
			return new Object[0];
		}

		Object[] values = new Object[args.length];
		for (int i = 0; i < values.length; i++) {
			if (args[i] == null) {
				values[i] = null;
				continue;
			}

			if (isPrimitiveOrWrapper(types[i])) {
				values[i] = args[i];
				continue;
			}

			values[i] = types[i].cast(args[i]);
		}
		return values;
	}
}