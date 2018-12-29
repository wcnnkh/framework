package scw.common.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.common.ClassInfo;
import scw.spring.core.LocalVariableTableParameterNameDiscoverer;

public final class ClassUtils {
	public static final String ALL_PACKAGE_NAME = "*";
	public static final String PROXY_CLASS_SPLIT = "$$";//代理类的分割符
	private volatile static Map<String, ClassInfo> clzMap = new HashMap<String, ClassInfo>();
	private static Map<Class<?>, Class<?>> basicTypeMap = new HashMap<Class<?>, Class<?>>();
	private static Map<Class<?>, Class<?>> basicValueTypeMap = new HashMap<Class<?>, Class<?>>();
	private static Map<String, Class<?>> basicValueTypeNameMap = new HashMap<String, Class<?>>();
	private static Map<String, Class<?>> classForNameMap = new HashMap<String, Class<?>>();
	private static LocalVariableTableParameterNameDiscoverer lvtpnd = new LocalVariableTableParameterNameDiscoverer();

	static {
		registerBasicType(Character.class, char.class);
		registerBasicType(Byte.class, byte.class);
		registerBasicType(Short.class, short.class);
		registerBasicType(Integer.class, int.class);
		registerBasicType(Long.class, long.class);
		registerBasicType(Float.class, float.class);
		registerBasicType(Double.class, double.class);
		registerBasicType(Boolean.class, boolean.class);
	}

	private ClassUtils() {
	};

	private static void registerBasicType(Class<?> type1, Class<?> type2) {
		basicTypeMap.put(type1, type2);
		basicValueTypeMap.put(type2, type1);
		basicValueTypeNameMap.put(type2.getName(), type2);
	}

	/**
	 * 是否是一个基本数据类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBasicType(Class<?> type) {
		return containsBasicType(type) || containsBasicValueType(type);
	}

	/**
	 * 是否是基本数据类型(引用类型)
	 * 
	 * @param type
	 * @return
	 */
	public static boolean containsBasicType(Class<?> type) {
		return basicTypeMap.containsKey(type);
	}

	/**
	 * 是否是一个基本数据类型(值类型)
	 * 
	 * @param type
	 * @return
	 */
	public static boolean containsBasicValueType(Class<?> type) {
		return basicValueTypeMap.containsKey(type);
	}

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
		return Character.class.isAssignableFrom(type)
				|| char.class.isAssignableFrom(type);
	}

	/**
	 * Byte || byte
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isByteType(Class<?> type) {
		return byte.class.isAssignableFrom(type)
				|| Byte.class.isAssignableFrom(type);
	}

	/**
	 * Short || short
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isShortType(Class<?> type) {
		return short.class.isAssignableFrom(type)
				|| Short.class.isAssignableFrom(type);
	}

	/**
	 * Integer || int
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isIntType(Class<?> type) {
		return int.class.isAssignableFrom(type)
				|| Integer.class.isAssignableFrom(type);
	}

	/**
	 * Long || long
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isLongType(Class<?> type) {
		return long.class.isAssignableFrom(type)
				|| Long.class.isAssignableFrom(type);
	}

	/**
	 * Float || float
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isFloatType(Class<?> type) {
		return float.class.isAssignableFrom(type)
				|| Float.class.isAssignableFrom(type);
	}

	/**
	 * Double || double
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isDoubleType(Class<?> type) {
		return double.class.isAssignableFrom(type)
				|| Double.class.isAssignableFrom(type);
	}

	/**
	 * Boolean || boolean
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBooleanType(Class<?> type) {
		return boolean.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type);
	}

	/**
	 * 获取类信息，先会从缓存中查找
	 * 
	 * @param clz
	 * @return
	 */
	public static ClassInfo getClassInfo(Class<?> clz) {
		return getClassInfo(getProxyRealClassName(clz.getName()));
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
						Class<?> clz = Class.forName(name);
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

	public static <T> Class<T> forName(String name)
			throws ClassNotFoundException {
		return forName(name, getDefaultClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(final String name,
			final ClassLoader loader) throws ClassNotFoundException {
		if (basicValueTypeNameMap.containsKey(name)) {
			return (Class<T>) basicValueTypeNameMap.get(name);
		}

		if (classForNameMap.containsKey(name)) {
			return (Class<T>) classForNameMap.get(name);
		}

		return (Class<T>) Class.forName(name, true,
				loader == null ? getDefaultClassLoader() : loader);
	}

	public static <T> Class<T> forNameByCache(final String name)
			throws ClassNotFoundException {
		return forNameByCache(name, getDefaultClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forNameByCache(final String name,
			final ClassLoader loader) throws ClassNotFoundException {
		if (basicValueTypeNameMap.containsKey(name)) {
			return (Class<T>) basicValueTypeNameMap.get(name);
		}

		if (classForNameMap.containsKey(name)) {
			return (Class<T>) classForNameMap.get(name);
		} else {
			synchronized (classForNameMap) {
				Class<T> type = (Class<T>) Class.forName(name, true,
						loader == null ? getDefaultClassLoader() : loader);
				classForNameMap.put(name, type);
				return type;
			}
		}
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
						String beanName = fileName.substring(0,
								fileName.lastIndexOf("."));
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
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(packageDirName);
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
					findAndAddClassesInPackageByFile(packageName, filePath,
							recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection())
								.getJarFile();
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
									packageName = name.substring(0, idx)
											.replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class")
											&& !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(
												packageName.length() + 1,
												name.length() - 6);
										// 添加到classes
										Class<?> clz = null;
										try {
											clz = Class.forName(packageName
													+ '.' + className);
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
	private static void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, final boolean recursive, List<Class<?>> classes) {
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
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
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
		if (superClz == null
				|| Object.class.getName().equals(superClz.getName())) {
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
		int index = cglibName.indexOf(PROXY_CLASS_SPLIT);
		if (index == -1) {
			return cglibName;
		} else {
			return cglibName.substring(0, index);
		}
	}

	public static Long getSerialVersionUID(Class<?> clz) {
		try {
			Field field = clz.getField("serialVersionUID");
			if (field != null && Modifier.isStatic(field.getModifiers())
					&& Modifier.isFinal(field.getModifiers())
					&& long.class.isAssignableFrom(field.getType())) {
				return (Long) field.get(null);
			}
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
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
}
