package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.page.Browseable;
import run.soeasy.framework.core.page.CustomizeBrowseable;
import run.soeasy.framework.core.page.CustomizeCursor;

public final class ClassUtils {
	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * Map with common "java.lang" class name as key and corresponding Class as
	 * value. Primarily for efficient deserialization of remote invocations.
	 */
	private static final Map<String, Class<?>> commonClassCache = new HashMap<String, Class<?>>(32);

	private static final Class<?>[] EMPTY_ARRAY = new Class<?>[0];

	public static final String GENERIC_PREFIX = "<";

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/**
	 * Map with primitive type name as key and corresponding primitive type as
	 * value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

	/**
	 * Map with primitive type as key and corresponding wrapper type as value, for
	 * example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

	/**
	 * Map with primitive wrapper type as key and corresponding primitive type as
	 * value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

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

		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(64);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(Arrays.asList(new Class<?>[] { boolean[].class, byte[].class, char[].class,
				double[].class, float[].class, int[].class, long[].class, short[].class }));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}

		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class,
				Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class,
				Object.class, Object[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class,
				StackTraceElement.class, StackTraceElement[].class);
		registerCommonClasses(Enum.class, Iterable.class, Cloneable.class, Comparable.class);
	}

	/**
	 * Given an input class object, return a string which consists of the class's
	 * package name as a pathname, i.e., all dots ('.') are replaced by slashes
	 * ('/'). Neither a leading nor trailing slash is added. The result could be
	 * concatenated with a slash and the name of a resource and fed directly to
	 * {@code ClassLoader.getResource()}. For it to be fed to
	 * {@code Class.getResource} instead, a leading slash would also have to be
	 * prepended to the returned value.
	 * 
	 * @param clazz the input class. A {@code null} value or the default (empty)
	 *              package will result in an empty string ("") being returned.
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

	@SuppressWarnings("unchecked")
	public static <T> Class<T>[] emptyArray() {
		return (Class<T>[]) EMPTY_ARRAY;
	}

	/**
	 * Replacement for {@code Class.forName()} that also returns Class instances for
	 * primitives (e.g. "int") and array class names (e.g. "String[]"). Furthermore,
	 * it is also capable of resolving inner class names in Java source style (e.g.
	 * "java.lang.Thread.State" instead of "java.lang.Thread$State").
	 * 
	 * @param name        the name of the Class
	 * @param classLoader the class loader to use (may be {@code null}, which
	 *                    indicates the default class loader)
	 * @return a class instance for the supplied name
	 * @throws ClassNotFoundException if the class was not found
	 * @throws LinkageError           if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 */
	public static Class<?> forName(@NonNull String name, ClassLoader classLoader)
			throws ClassNotFoundException, LinkageError {
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
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
			String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[[I" or "[[Ljava.lang.String;" style arrays
		if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
			String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		int end = name.indexOf(GENERIC_PREFIX);
		if (end != -1) {
			// 对于泛型字符串处理
			int begin = name.lastIndexOf(" ", end);
			return forName(name.substring(begin == -1 ? 0 : begin + 1, end), classLoader);
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return forName0(name, classLoaderToUse);
		} catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
				// try {
				return forName0(innerClassName, classLoaderToUse);
				// } catch (ClassNotFoundException ex2) {
				// swallow - let original exception get through
				// }
			}
			throw ex;
		}
	}

	private static Class<?> forName0(String name, ClassLoader classLoader) throws ClassNotFoundException {
		return Class.forName(name, false, classLoader);
	}

	/**
	 * 如果类不存在将返回空
	 * 
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static Class<?> getClass(@NonNull String className, ClassLoader classLoader) {
		Class<?> clazz = null;
		try {
			clazz = forName(className, classLoader);
		} catch (IllegalAccessError err) {
			throw new IllegalStateException(
					"Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
					err);
		} catch (Throwable ex) {
			// Typically ClassNotFoundException or NoClassDefFoundError...
		}
		return clazz;
	}

	public static Optional<Class<?>> findClass(@NonNull String className, ClassLoader classLoader) {
		try {
			return Optional.ofNullable(forName(className, classLoader));
		} catch (IllegalAccessError err) {
			throw new IllegalStateException(
					"Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
					err);
		} catch (Throwable ex) {
			// Typically ClassNotFoundException or NoClassDefFoundError...
		}
		return Optional.empty();
	}

	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils class
	 * will be used as fallback.
	 * <p>
	 * Call this method if you intend to use the thread context ClassLoader in a
	 * scenario where you absolutely need a non-null ClassLoader reference: for
	 * example, for class path resource loading (but not necessarily for
	 * {@code Class.forName}, which accepts a {@code null} ClassLoader reference as
	 * well).
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

	public static Browseable<Class<?>, Class<?>> getInterfaces(@NonNull Class<?> sourceClass) {
		return new CustomizeBrowseable<Class<?>, Class<?>>(sourceClass, (c) -> {
			Class<?>[] interfaces = c.getInterfaces();
			List<Class<?>> list = interfaces == null ? Collections.emptyList() : Arrays.asList(interfaces);
			return new CustomizeCursor<>(c, Elements.of(list), c.getSuperclass());
		});
	}

	/**
	 * Return the qualified name of the given class: usually simply the class name,
	 * but component type class name + "[]" for arrays.
	 * 
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(@NonNull Class<?> clazz) {
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Build a nice qualified name for an array: component type class name + "[]".
	 * 
	 * @param clazz the array class
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
	 * Check if the right-hand side type may be assigned to the left-hand side type,
	 * assuming setting by reflection. Considers primitive wrapper classes as
	 * assignable to the corresponding primitive types.
	 * 
	 * @param lhsType the target type
	 * @param rhsType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isAssignable(@NonNull Class<?> lhsType, @NonNull Class<?> rhsType) {
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

	public static boolean isAssignable(Class<?>[] lhsTypes, Class<?>[] rhsTypes) {
		if (ArrayUtils.isEmpty(lhsTypes)) {
			return ArrayUtils.isEmpty(rhsTypes);
		}

		if (lhsTypes.length != (rhsTypes == null ? 0 : rhsTypes.length)) {
			return false;
		}

		for (int i = 0; i < lhsTypes.length; i++) {
			if (!isAssignable(lhsTypes[i], rhsTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the given type is assignable from the given value, assuming
	 * setting by reflection. Considers primitive wrapper classes as assignable to
	 * the corresponding primitive types.
	 * 
	 * @param type  the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(@NonNull Class<?> type, Object value) {
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	public static boolean isBoolean(Type type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isByte(Type type) {
		return type == byte.class || type == Byte.class;
	}

	public static boolean isChar(Type type) {
		return type == char.class || type == Character.class;
	}

	public static boolean isDouble(Type type) {
		return type == double.class || type == Double.class;
	}

	public static boolean isFloat(Type type) {
		return type == float.class || type == Float.class;
	}

	public static boolean isInt(Type type) {
		return type == int.class || type == Integer.class;
	}

	public static boolean isLong(Type type) {
		return type == long.class || type == Long.class;
	}

	/**
	 * 是否是一个有多个值的类型
	 * 
	 * @see Collection
	 * @see Array
	 * @param type
	 * @return
	 */
	public static boolean isMultipleValues(Class<?> type) {
		return type != null && (type.isArray() || Collection.class.isAssignableFrom(type));
	}
	
	public static boolean isPrimitive(Type type) {
		return type instanceof Class && ((Class<?>) type).isPrimitive();
	}

	/**
	 * Check if the given class represents an array of primitives, i.e. boolean,
	 * byte, char, short, int, long, float, or double.
	 * 
	 * @param clazz the class to check
	 * @return whether the given class is a primitive array class
	 */
	public static boolean isPrimitiveArray(@NonNull Class<?> clazz) {
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * Check if the given class represents a primitive (i.e. boolean, byte, char,
	 * short, int, long, float, or double) or a primitive wrapper (i.e. Boolean,
	 * Byte, Character, Short, Integer, Long, Float, or Double).
	 * 
	 * @param type the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(@NonNull Type type) {
		return isPrimitive(type) || isPrimitiveWrapper(type);
	}

	/**
	 * Check if the given class represents a primitive wrapper, i.e. Boolean, Byte,
	 * Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param type the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(@NonNull Type type) {
		return primitiveWrapperTypeMap.containsKey(type);
	}

	/**
	 * Check if the given class represents an array of primitive wrappers, i.e.
	 * Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(@NonNull Class<?> clazz) {
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	public static boolean isShort(Type type) {
		return type == short.class || type == Short.class;
	}

	public static boolean isString(Type type) {
		return type == String.class;
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
	 * Resolve the given class name into a Class instance. Supports primitives (like
	 * "int") and array class names (like "String[]").
	 * <p>
	 * This is effectively equivalent to the {@code forName} method with the same
	 * arguments, with the only difference being the exceptions thrown in case of
	 * class loading failure.
	 * 
	 * @param className   the name of the Class
	 * @param classLoader the class loader to use (may be {@code null}, which
	 *                    indicates the default class loader)
	 * @return a class instance for the supplied name
	 * @throws IllegalArgumentException if the class name was not resolvable (that
	 *                                  is, the class could not be found or the
	 *                                  class file could not be loaded)
	 * @throws IllegalStateException    if the corresponding class is resolvable but
	 *                                  there was a readability mismatch in the
	 *                                  inheritance hierarchy of the class
	 *                                  (typically a missing dependency declaration
	 *                                  in a Jigsaw module definition for a
	 *                                  superclass or interface implemented by the
	 *                                  class to be loaded here)
	 * @see #forName(String, ClassLoader)
	 */
	public static Class<?> resolveClassName(@NonNull String className, ClassLoader classLoader)
			throws IllegalArgumentException {

		try {
			return forName(className, classLoader);
		} catch (IllegalAccessError err) {
			throw new IllegalStateException(
					"Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
					err);
		} catch (LinkageError err) {
			throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
		}
	}

	/**
	 * Resolve the given class name as primitive class, if appropriate, according to
	 * the JVM's naming rules for primitive classes.
	 * <p>
	 * Also supports the JVM's internal class names for primitive arrays. Does
	 * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
	 * only supported by {@link #forName(String, ClassLoader)}.
	 * 
	 * @param name the name of the potentially primitive class
	 * @return the primitive class, or {@code null} if the name does not denote a
	 *         primitive class or primitive array class
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
	 * Resolve the given class if it is a primitive class, returning the
	 * corresponding primitive wrapper type instead.
	 * 
	 * @param clazz the class to check
	 * @return the original class, or a primitive wrapper for the original primitive
	 *         type
	 */
	public static Class<?> resolvePrimitiveIfNecessary(@NonNull Class<?> clazz) {
		return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
	}

	/**
	 * Determine if the supplied {@link Class} is a JVM-generated implementation
	 * class for a lambda expression or method reference.
	 * <p>
	 * This method makes a best-effort attempt at determining this, based on checks
	 * that work on modern, mainstream JVMs.
	 * <p>
	 * 如果要判断是否是指定类型的lambda表达式可以通过{@link Class#getInterfaces()}解决
	 * 
	 * @param clazz the class to check
	 * @return {@code true} if the class is a lambda implementation class
	 */
	public static boolean isLambdaClass(Class<?> clazz) {
		return (clazz.isSynthetic() && (clazz.getSuperclass() == Object.class) && (clazz.getInterfaces().length > 0)
				&& clazz.getName().contains("$$Lambda"));
	}

	private ClassUtils() {
	}
}
