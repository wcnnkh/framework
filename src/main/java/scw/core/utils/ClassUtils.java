package scw.core.utils;

import java.beans.Introspector;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.cglib.core.TypeUtils;
import scw.core.ClassInfo;
import scw.core.LocalVariableTableParameterNameDiscoverer;

public final class ClassUtils {
	public static final String ALL_PACKAGE_NAME = "*";

	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";

	private volatile static Map<String, ClassInfo> clzMap = new HashMap<String, ClassInfo>();
	private static LocalVariableTableParameterNameDiscoverer lvtpnd = new LocalVariableTableParameterNameDiscoverer();

	/**
	 * Map with primitive wrapper type as key and corresponding primitive type
	 * as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);

	/**
	 * Map with primitive type as key and corresponding wrapper type as value,
	 * for example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);

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
	 * String
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isStringType(Class<?> type) {
		return String.class.isAssignableFrom(type);
	}

	/**
	 * Character || char
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isCharType(Class<?> type) {
		return Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type);
	}

	/**
	 * Byte || byte
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isByteType(Class<?> type) {
		return byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type);
	}

	/**
	 * Short || short
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isShortType(Class<?> type) {
		return short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type);
	}

	/**
	 * Integer || int
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isIntType(Class<?> type) {
		return int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type);
	}

	/**
	 * Long || long
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isLongType(Class<?> type) {
		return long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type);
	}

	/**
	 * Float || float
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isFloatType(Class<?> type) {
		return float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type);
	}

	/**
	 * Double || double
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isDoubleType(Class<?> type) {
		return double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type);
	}

	/**
	 * Boolean || boolean
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBooleanType(Class<?> type) {
		return boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
	}

	/**
	 * 获取类信息，先会从缓存中查找
	 * 
	 * @param clz
	 * @return
	 */
	public static ClassInfo getClassInfo(Class<?> clz) {
		return getClassInfo(clz.getName());
	}

	/**
	 * 获取类信息，先会从缓存中查找
	 * 
	 * @param className
	 * @return
	 */
	public static ClassInfo getClassInfo(String name) {
		ClassInfo info = clzMap.get(name);
		if (info == null) {
			synchronized (clzMap) {
				info = clzMap.get(name);
				if (info == null) {
					try {
						Class<?> clz = Class.forName(getProxyRealClassName(name));
						info = new ClassInfo(clz);
						clzMap.put(name, info);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return info;
	}

	/**
	 * 获取指定包下的类型，如获取全部类则不包含jar包中的类
	 * 
	 * @param packageName
	 *            多个可以使用;(分号)隔开
	 * @return
	 */
	public static Collection<Class<?>> getClasses(String packageName) {
		if (StringUtils.isNull(packageName)) {
			return getClassListForPackageArray();
		} else if (ALL_PACKAGE_NAME.equals(packageName)) {
			return getClassesForPackage("");
		} else {
			Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
			String[] pArr = StringUtils.commonSplit(packageName);
			Map<String, Boolean> tagMap = new HashMap<String, Boolean>();
			for (String pg : pArr) {
				if (tagMap.containsKey(pg)) {
					continue;
				}
				tagMap.put(pg, true);

				List<Class<?>> classes;
				if ("".equals(pg)) {// all
					classes = getClassListForPackageArray();
				} else {
					classes = getClassesForPackage(pg);
				}

				for (Class<?> clz : classes) {
					if (classMap.containsKey(clz.getName())) {
						continue;
					}
					classMap.put(clz.getName(), clz);
				}
			}
			return classMap.values();
		}
	}

	private static List<Class<?>> getClassListForPackageArray(String... pages) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		StringBuilder beanPath = new StringBuilder(ConfigUtils.getClassPath());
		StringBuilder pagePath = new StringBuilder();
		for (int i = 0; i < pages.length; i++) {
			String page = pages[i];

			beanPath.append(page);
			beanPath.append(File.separator);

			pagePath.append(page);
			pagePath.append(".");
		}

		String rootPage = pagePath.toString();
		File rootFile = new File(beanPath.toString());
		if (rootFile.exists()) {
			File[] files = rootFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile()) {
					String fileName = file.getName();
					if (fileName.endsWith(".class")) {
						String beanName = fileName.substring(0, fileName.lastIndexOf("."));
						Class<?> clz = null;
						try {
							clz = Class.forName(rootPage + beanName);
						} catch (Throwable e) {
							continue;
						}
						if (clz != null) {
							classList.add(clz);
						}
					}
				} else if (file.isDirectory()) {
					String[] pageArr = new String[pages.length + 1];
					for (int k = 0; k < pages.length; k++) {
						pageArr[k] = pages[k];
					}

					pageArr[pages.length] = file.getName();
					classList.addAll(getClassListForPackageArray(pageArr));
				}
			}
		}
		return classList;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @return
	 */
	private static List<Class<?>> getClassesForPackage(String packageName) {
		// 第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										// 添加到classes
										Class<?> clz = null;
										try {
											clz = Class.forName(packageName + '.' + className);
										} catch (Throwable e) {
										}

										if (clz != null) {
											classes.add(clz);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, List<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				// 添加到集合中去
				if (packageName.startsWith(".")) {
					packageName = packageName.substring(1);
				}

				Class<?> clz = null;
				try {
					clz = Class.forName(packageName + '.' + className);
				} catch (Throwable e) {
				}

				if (clz != null) {
					classes.add(clz);
				}
			}
		}
	}

	public static String[] getParameterName(Method method) {
		return lvtpnd.getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterName(Constructor constructor) {
		return lvtpnd.getParameterNames(constructor);
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
	 * 是否可以实例化
	 * 
	 * @return
	 */
	public static boolean isInstance(Class<?> clz) {
		return !(Modifier.isAbstract(clz.getModifiers()) || Modifier.isInterface(clz.getModifiers()));
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
			// No thread context class loader -> use class loader of this class.
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

	public static Class<?> forName(String name) throws ClassNotFoundException, LinkageError {
		return forName(name, getDefaultClassLoader());
	}

	public static Class<?>[] forName(String... className) throws ClassNotFoundException {
		Class<?>[] types = new Class<?>[className.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = forName(className[i]);
		}
		return types;
	}

	/**
	 * Replacement for {@code Class.forName()} that also returns Class instances
	 * for primitives (e.g."int") and array class names (e.g. "String[]").
	 * Furthermore, it is also capable of resolving inner class names in Java
	 * source style (e.g. "java.lang.Thread.State" instead of
	 * "java.lang.Thread$State").
	 * 
	 * @param name
	 *            the name of the Class
	 * @param classLoader
	 *            the class loader to use (may be {@code null}, which indicates
	 *            the default class loader)
	 * @return Class instance for the supplied name
	 * @throws ClassNotFoundException
	 *             if the class was not found
	 * @throws LinkageError
	 *             if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 */
	public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
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

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return classLoaderToUse.loadClass(name);
		} catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
				// try {
				return classLoaderToUse.loadClass(innerClassName);
				// } catch (ClassNotFoundException ex2) {
				// swallow - let original exception get through
				// }
			}
			throw ex;
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
	 * Determine whether the {@link Class} identified by the supplied name is
	 * present and can be loaded. Will return {@code false} if either the class
	 * or one of its dependencies is not present or cannot be loaded.
	 * 
	 * @param className
	 *            the name of the class to check
	 * @return whether the specified class is present
	 * @deprecated as of Spring 2.5, in favor of
	 *             {@link #isPresent(String, ClassLoader)}
	 */
	@Deprecated
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
			forName(className, classLoader);
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

	/**
	 * 是否是数字类型，不包含char,boolean
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumberType(Class<?> type) {
		if (Number.class.isAssignableFrom(type)) {
			return true;
		}

		return isIntType(type) || isLongType(type) || isShortType(type) || isFloatType(type) || isDoubleType(type)
				|| isByteType(type);
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
}
