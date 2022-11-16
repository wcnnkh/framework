package io.basc.framework.util;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.classreading.SimpleMetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.page.Pageables;
import io.basc.framework.util.page.SharedPageable;
import io.basc.framework.util.page.StreamPageables;

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

	/** The inner class separator character: '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/** The package separator character: '.' */
	private static final char PACKAGE_SEPARATOR = '.';

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
	 * Return a path suitable for use with {@code ClassLoader.getResource} (also
	 * suitable for use with {@code Class.getResource} by prepending a slash ('/')
	 * to the return value). Built by taking the package of the specified class
	 * file, converting all dots ('.') to slashes ('/'), adding a trailing slash if
	 * necessary, and concatenating the specified resource name to this. <br/>
	 * 
	 * @param clazz        the Class whose package will be used as the base
	 * @param resourceName the resource name to append. A leading slash is optional.
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
	};

	/**
	 * Build a String that consists of the names of the classes/interfaces in the
	 * given array.
	 * <p>
	 * Basically like {@code AbstractCollection.toString()}, but stripping the
	 * "class "/"interface " prefix before every class name.
	 * 
	 * @param classes a Collection of Class objects (may be {@code null})
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	@SuppressWarnings("rawtypes")
	public static String classNamesToString(Class... classes) {
		return classNamesToString(Arrays.asList(classes));
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces in the
	 * given collection.
	 * <p>
	 * Basically like {@code AbstractCollection.toString()}, but stripping the
	 * "class "/"interface " prefix before every class name.
	 * 
	 * @param classes a Collection of Class objects (may be {@code null})
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

	/**
	 * Convert a "."-based fully qualified class name to a "/"-based resource path.
	 * 
	 * @param className the fully qualified class name
	 * @return the corresponding resource path, pointing to the class
	 */
	public static String convertClassNameToResourcePath(String className) {
		Assert.notNull(className, "Class name must not be null");
		return className.replace('.', '/');
	}

	/**
	 * Convert a "/"-based resource path to a "."-based fully qualified class name.
	 * 
	 * @param resourcePath the resource path pointing to a class
	 * @return the corresponding fully qualified class name
	 */
	public static String convertResourcePathToClassName(String resourcePath) {
		Assert.notNull(resourcePath, "Resource path must not be null");
		return resourcePath.replace('/', '.');
	}

	/**
	 * Determine the common ancestor of the given classes, if any.
	 * 
	 * @param clazz1 the class to introspect
	 * @param clazz2 the other class to introspect
	 * @return the common ancestor (i.e. common superclass, one interface extending
	 *         the other), or {@code null} if none found. If any of the given
	 *         classes is {@code null}, the other class will be returned.
	 */
	public static Class<?> determineCommonAncestor(Class<?> clazz1, Class<?> clazz2) {
		if (clazz1 == null) {
			return clazz2;
		}
		if (clazz2 == null) {
			return clazz1;
		}
		if (clazz1.isAssignableFrom(clazz2)) {
			return clazz1;
		}
		if (clazz2.isAssignableFrom(clazz1)) {
			return clazz2;
		}
		Class<?> ancestor = clazz1;
		do {
			ancestor = ancestor.getSuperclass();
			if (ancestor == null || Object.class == ancestor) {
				return null;
			}
		} while (!ancestor.isAssignableFrom(clazz2));
		return ancestor;
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
	public static Class<?> forName(String name, @Nullable ClassLoader classLoader)
			throws ClassNotFoundException, LinkageError {
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

	public static Class<?>[] forNames(ClassLoader classLoader, String... className) throws ClassNotFoundException {
		if (ArrayUtils.isEmpty(className)) {
			return new Class<?>[0];
		}

		Class<?>[] types = new Class<?>[className.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = forName(className[i], classLoader);
		}
		return types;
	}

	@Nullable
	public static Class<?> forResource(@Nullable Resource resource, @Nullable ClassLoader classLoader,
			@Nullable MetadataReaderFactory metadataReaderFactory, @Nullable TypeFilter typeFilter) throws IOException {
		if (resource == null) {
			return null;
		}

		MetadataReaderFactory factory = metadataReaderFactory;
		if (factory == null) {
			factory = new SimpleMetadataReaderFactory(classLoader);
		}

		MetadataReader reader = factory.getMetadataReader(resource);
		if (reader == null) {
			return null;
		}

		if (typeFilter != null && !typeFilter.match(reader, factory)) {
			return null;
		}

		return ClassUtils.getClass(reader.getClassMetadata().getClassName(), classLoader);
	}

	public static Stream<Class<?>> forResources(ResourceLoader resourceLoader, Stream<Resource> resources,
			@Nullable ClassLoader classLoader, @Nullable MetadataReaderFactory metadataReaderFactory,
			@Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(resourceLoader != null, "resourceLoader");
		Assert.requiredArgument(resources != null, "resources");
		MetadataReaderFactory factory = metadataReaderFactory == null ? new SimpleMetadataReaderFactory(resourceLoader)
				: metadataReaderFactory;
		ClassLoader cl = classLoader == null ? resourceLoader.getClassLoader() : classLoader;
		Stream<Class<?>> stream = resources.map((resource) -> {
			try {
				return forResource(resource, cl, factory, typeFilter);
			} catch (IOException e) {
				return null;
			}
		});
		return stream.filter((e) -> e != null);
	}

	public static Stream<Class<?>> forResources(ResourcePatternResolver resourcePatternResolver, String locationPattern,
			@Nullable ClassLoader classLoader, @Nullable MetadataReaderFactory metadataReaderFactory,
			@Nullable TypeFilter typeFilter) throws IOException {
		Assert.requiredArgument(resourcePatternResolver != null, "resourcePatternResolver");
		Assert.requiredArgument(StringUtils.isNotEmpty(locationPattern), "locationPattern");
		Resource[] resources = resourcePatternResolver.getResources(locationPattern);
		if (resources == null || resources.length == 0) {
			return XUtils.emptyStream();
		}

		MetadataReaderFactory factory = metadataReaderFactory;
		if (factory == null) {
			factory = new SimpleMetadataReaderFactory(resourcePatternResolver);
		}

		ClassLoader cl = classLoader;
		if (cl == null) {
			cl = resourcePatternResolver.getClassLoader();
		}
		return forResources(Arrays.asList(resources).stream(), classLoader, factory, typeFilter);
	}

	public static Stream<Class<?>> forResources(Stream<Resource> resources, @Nullable ClassLoader classLoader,
			@Nullable MetadataReaderFactory metadataReaderFactory, @Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(resources != null, "resources");
		MetadataReaderFactory factory = metadataReaderFactory;
		if (factory == null) {
			factory = new SimpleMetadataReaderFactory(classLoader);
		}

		Stream<Class<?>> stream = resources.map((resource) -> {
			try {
				return forResource(resource, classLoader, metadataReaderFactory, typeFilter);
			} catch (IOException e) {
				return null;
			}
		});
		return stream.filter((e) -> e != null);
	}

	/**
	 * Return all interfaces that the given instance implements as array, including
	 * ones implemented by superclasses.
	 * 
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as array
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] getAllInterfaces(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * Return all interfaces that the given instance implements as Set, including
	 * ones implemented by superclasses.
	 * 
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as Set
	 */
	@SuppressWarnings("rawtypes")
	public static Set<Class> getAllInterfacesAsSet(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	/**
	 * Return all interfaces that the given class implements as array, including
	 * ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz the class to analyze for interfaces
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
	 * @param clazz       the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 *                    (may be {@code null} when accepting all declared
	 *                    interfaces)
	 * @return all interfaces that the given object implements as array
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
		Set<Class> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
		return ifcs.toArray(new Class[ifcs.size()]);
	}

	/**
	 * 获取一个类的所有接口 Return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>
	 * If the class itself is an interface, it gets returned as sole interface.
	 * 
	 * @param clazz the class to analyze for interfaces
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
	 * @param clazz       the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 *                    (may be {@code null} when accepting all declared
	 *                    interfaces)
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
	 * 如果类不存在将返回空
	 * 
	 * @param name
	 * @param classLoader
	 * @return
	 */
	@Nullable
	public static Class<?> getClass(String className, @Nullable ClassLoader classLoader) {
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

	/**
	 * Determine the name of the class file, relative to the containing package:
	 * e.g. "String.class"
	 * 
	 * @param clazz the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	/**
	 * @see #getDefaultClassLoader()
	 * @param classLoaderProvider
	 * @return
	 */
	public static ClassLoader getClassLoader(ClassLoaderProvider classLoaderProvider) {
		ClassLoader classLoader = classLoaderProvider == null ? null : classLoaderProvider.getClassLoader();
		return classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
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

	/**
	 * Return a descriptive name for the given object's type: usually simply the
	 * class name, but component type class name + "[]" for arrays, and an appended
	 * list of implemented interfaces for JDK proxies.
	 * 
	 * @param value the value to introspect
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

	public static Pageables<Class<?>, Class<?>> getInterfaces(Class<?> sourceClass) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new StreamPageables<Class<?>, Class<?>>(sourceClass, (c) -> {
			Class<?>[] interfaces = c.getInterfaces();
			List<Class<?>> list = interfaces == null ? Collections.emptyList() : Arrays.asList(interfaces);
			return new SharedPageable<>(c, list, c.getSuperclass());
		});
	}

	/**
	 * Determine the name of the package of the given class, e.g. "java.lang" for
	 * the {@code java.lang.String} class.
	 * 
	 * @param clazz the class
	 * @return the package name, or the empty String if the class is defined in the
	 *         default package
	 */
	public static String getPackageName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return getPackageName(clazz.getName());
	}

	/**
	 * Determine the name of the package of the given fully-qualified class name,
	 * e.g. "java.lang" for the {@code java.lang.String} class name.
	 * 
	 * @param fqClassName the fully-qualified class name
	 * @return the package name, or the empty String if the class is defined in the
	 *         default package
	 */
	public static String getPackageName(String fqClassName) {
		Assert.notNull(fqClassName, "Class name must not be null");
		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
	}

	/**
	 * 获取所有父包名
	 * 
	 * @param packageName
	 * @return
	 */
	public static String[] getParentPackageNames(String packageName) {
		if (!StringUtils.hasText(packageName)) {
			return new String[0];
		}

		String[] packageNameArray = StringUtils.splitToArray(packageName, ".");
		String[] array = new String[packageNameArray.length - 1];
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = packageNameArray.length; i < len; i++) {
			if (i != 0) {
				sb.append(".");
			}

			if (i == len - 1) {
				break;
			}

			sb.append(packageNameArray[i]);
			array[i] = sb.toString();
		}
		return array;
	}

	/**
	 * Return the qualified name of the given class: usually simply the class name,
	 * but component type class name + "[]" for arrays.
	 * 
	 * @param clazz the class
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
	 * Get the class name without the qualified package name.
	 * 
	 * @param clazz the class to get the short name for
	 * @return the class name of the class without the package name
	 */
	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	/**
	 * Get the class name without the qualified package name.
	 * 
	 * @param className the className to get the short name for
	 * @return the class name of the class without the package name
	 * @throws IllegalArgumentException if the className is empty
	 */
	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		String shortName = className.substring(lastDotIndex + 1, className.length());
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	/**
	 * Return the short string name of a Java class in uncapitalized JavaBeans
	 * property format. Strips the outer class name in case of an inner class.
	 * 
	 * @param clazz the class
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

	/**
	 * Check if the right-hand side type may be assigned to the left-hand side type,
	 * assuming setting by reflection. Considers primitive wrapper classes as
	 * assignable to the corresponding primitive types.
	 * 
	 * @param lhsType the target type
	 * @param rhsType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 * @see CGLIBTypeUtils#isAssignable
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
	 * setting by reflection. Considers primitive wrapper classes as assignable to
	 * the corresponding primitive types.
	 * 
	 * @param type  the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(Class<?> type, Object value) {
		Assert.notNull(type, "Type must not be null");
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	/**
	 * 此类是否可用
	 * 
	 * @see Ignore
	 * @see JavaVersion#isSupported(Class)
	 * @param clazz
	 * @return
	 */
	public static boolean isAvailable(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return JavaVersion.isSupported(clazz);
	}

	public static boolean isBoolean(Type type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isByte(Type type) {
		return type == byte.class || type == Byte.class;
	}

	/**
	 * Check whether the given class is cache-safe in the given context, i.e.
	 * whether it is loaded by the given ClassLoader or a parent of it.
	 * 
	 * @param clazz       the class to analyze
	 * @param classLoader the ClassLoader to potentially cache metadata in
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

	/**
	 * Determine whether the {@link Class} identified by the supplied name is
	 * present and can be loaded. Will return {@code false} if either the class or
	 * one of its dependencies is not present or cannot be loaded.
	 * 
	 * @param className   the name of the class to check
	 * @param classLoader the class loader to use (may be {@code null} which
	 *                    indicates the default class loader)
	 * @return whether the specified class is present (including all of its
	 *         superclasses and interfaces)
	 * @throws IllegalStateException if the corresponding class is resolvable but
	 *                               there was a readability mismatch in the
	 *                               inheritance hierarchy of the class (typically a
	 *                               missing dependency declaration in a Jigsaw
	 *                               module definition for a superclass or interface
	 *                               implemented by the class to be checked here)
	 */
	public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
		try {
			forName(className, classLoader);
			return true;
		} catch (IllegalAccessError err) {
			throw new IllegalStateException(
					"Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
					err);
		} catch (Throwable ex) {
			// Typically ClassNotFoundException or NoClassDefFoundError...
			return false;
		}
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
	public static boolean isPrimitiveArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * Check if the given class represents a primitive (i.e. boolean, byte, char,
	 * short, int, long, float, or double) or a primitive wrapper (i.e. Boolean,
	 * Byte, Character, Short, Integer, Long, Float, or Double).
	 * 
	 * @param clazz the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(Type type) {
		Assert.notNull(type, "Class must not be null");
		return isPrimitive(type) || isPrimitiveWrapper(type);
	}

	/**
	 * Check if the given class represents a primitive wrapper, i.e. Boolean, Byte,
	 * Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(Type type) {
		Assert.notNull(type, "Class must not be null");
		return primitiveWrapperTypeMap.containsKey(type);
	}

	/**
	 * Check if the given class represents an array of primitive wrappers, i.e.
	 * Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * 
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	public static boolean isShort(Type type) {
		return type == short.class || type == Short.class;
	}

	public static boolean isString(Type type) {
		return type == String.class;
	}

	/**
	 * Check whether the given class is visible in the given ClassLoader.
	 * 
	 * @param clazz       the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against (may be {@code null}, in
	 *                    which case this method will always return {@code true})
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

	/**
	 * Check whether the given class matches the user-specified type name.
	 * 
	 * @param clazz    the class to check
	 * @param typeName the type name to match
	 */
	public static boolean matchesTypeName(Class<?> clazz, String typeName) {
		return (typeName != null && (typeName.equals(clazz.getName()) || typeName.equals(clazz.getSimpleName())
				|| (clazz.isArray() && typeName.equals(getQualifiedNameForArray(clazz)))));
	}

	/**
	 * Override the thread context ClassLoader with the environment's bean
	 * ClassLoader if necessary, i.e. if the bean ClassLoader is not equivalent to
	 * the thread context ClassLoader already.
	 * 
	 * @param classLoaderToUse the actual ClassLoader to use for the thread context
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

	public static boolean sameName(Class<?> left, Class<?> right) {
		if (left == right) {
			return true;
		}

		if (left == null) {
			return right == null;
		}

		if (right == null) {
			return left == null;
		}

		return StringUtils.equals(left.getName(), right.getName());
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
	public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader)
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
	public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
	}

	/**
	 * Copy the given Collection into a Class array. The Collection must contain
	 * Class elements only.
	 * 
	 * @param collection the Collection to copy
	 * @return the Class array ({@code null} if the passed-in Collection was
	 *         {@code null})
	 */
	public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new Class<?>[collection.size()]);
	}

	private ClassUtils() {
	}
}
