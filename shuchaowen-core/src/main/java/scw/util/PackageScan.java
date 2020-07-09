package scw.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import scw.core.GlobalPropertyFactory;
import scw.core.annotation.UseJavaVersion;
import scw.core.reflect.ReflectionUtils;
import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.classreading.SimpleMetadataReaderFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.io.support.PathMatchingResourcePatternResolver;
import scw.io.support.ResourcePatternResolver;
import scw.lang.Ignore;

public class PackageScan implements Accept<Class<?>> {
	public static final String ALL = "*";
	static final String CLASS_RESOURCE = "**/*.class";
	private final ResourcePatternResolver resourcePatternResolver;
	private final MetadataReaderFactory metadataReaderFactory;
	private boolean useCache = true;
	private String classDirectory;

	private static final PackageScan INSTANCE = new PackageScan(true);

	public static PackageScan getInstance() {
		return INSTANCE;
	}

	public PackageScan(boolean useCache) {
		this(new SimpleMetadataReaderFactory(), useCache);
	}

	public PackageScan(MetadataReaderFactory metadataReaderFactory, boolean useCache) {
		this(new PathMatchingResourcePatternResolver(), metadataReaderFactory, useCache);
	}

	public PackageScan(ResourcePatternResolver resourcePatternResolver, MetadataReaderFactory metadataReaderFactory,
			boolean useCache) {
		this.resourcePatternResolver = resourcePatternResolver;
		this.metadataReaderFactory = metadataReaderFactory;
		this.useCache = useCache;
	}

	public final ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	public final MetadataReaderFactory getMetadataReaderFactory() {
		return metadataReaderFactory;
	}

	public String getClassDirectory() {
		return classDirectory == null ? GlobalPropertyFactory.getInstance().getClassesDirectory() : classDirectory;
	}

	public void setClassDirectory(String classDirectory) {
		this.classDirectory = classDirectory;
	}

	protected Collection<Class<?>> getClassesInternal(String packageName) throws IOException {
		String usePackageName = packageName;
		if (StringUtils.isEmpty(usePackageName) || usePackageName.equals(ALL)) {
			usePackageName = ALL;
			String classDirectory = getClassDirectory();
			if (!StringUtils.isEmpty(classDirectory)) {
				Collection<Class<?>> classes = getDirectoryClasses(classDirectory,
						resourcePatternResolver.getClassLoader(), false);
				return classes;
			}
			return Collections.emptyList();
		} else {
			usePackageName = usePackageName.replace(".", "/");
		}

		if (!usePackageName.endsWith("/")) {
			usePackageName = usePackageName + "/";
		}

		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Resource resource : resourcePatternResolver
				.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + usePackageName + CLASS_RESOURCE)) {
			appendClass(resource, resourcePatternResolver.getClassLoader(), classes);
		}
		return classes;
	}

	private void appendClass(Resource resource, ClassLoader classLoader, Collection<Class<?>> classes)
			throws IOException {
		MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
		if (reader == null) {
			return;
		}

		Class<?> clazz = ClassUtils.forNameNullable(reader.getClassMetadata().getClassName(), classLoader);
		if (clazz == null) {
			return;
		}

		if (!accept(clazz)) {
			return;
		}

		classes.add(clazz);
	}

	public final Set<Class<?>> getClasses(Collection<String> packageNames) {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for (String packageName : packageNames) {
			for (String name : StringUtils.commonSplit(packageName)) {
				try {
					classes.addAll(useCache ? getClassesInternalUseCache(name) : getClassesInternal(name));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}

	public final Set<Class<?>> getClasses(String... packageNames) {
		return getClasses(Arrays.asList(packageNames));
	}

	private ConcurrentMap<String, Collection<Class<?>>> classCache = new ConcurrentReferenceHashMap<String, Collection<Class<?>>>();

	protected final Collection<Class<?>> getClassesInternalUseCache(String packageName) throws IOException {
		Collection<Class<?>> classes = getClassListByCache(packageName);
		if (classes == null) {
			String[] parentPackageNames = ClassUtils.getParentPackageNames(packageName);
			boolean sann = true;
			if (parentPackageNames.length != 0) {
				for (int len = parentPackageNames.length, i = len - 1; i >= 0; i--) {
					Collection<Class<?>> tempSet = getClassListByCache(parentPackageNames[i]);
					if (tempSet == null) {
						continue;
					}

					sann = false;
					classes = getSubSet(tempSet, packageName);
					break;
				}
			}

			if (sann) {
				classes = getClassesInternal(packageName);
				Collection<Class<?>> cache = classCache.putIfAbsent(packageName, classes);
				if (cache != null) {
					classes = cache;
				}
			}
		}
		return classes;
	}

	private final Collection<Class<?>> getClassListByCache(String packageName) {
		return classCache.get(packageName);
	}

	private final Collection<Class<?>> getSubSet(Collection<Class<?>> classes, String packageName) {
		HashSet<Class<?>> sets = new HashSet<Class<?>>();
		for (Class<?> clazz : classes) {
			if (clazz.getName().startsWith(packageName)) {
				sets.add(clazz);
			}
		}
		return sets;
	}

	/**
	 * 获取目录下的class
	 * 
	 * @param directorey
	 * @param prefix
	 * @return
	 */
	protected final Collection<Class<?>> getDirectoryClasses(String directory, ClassLoader classLoader,
			boolean initialize) {
		List<Class<?>> list = new LinkedList<Class<?>>();
		appendDirectoryClass(null, new File(directory), list, classLoader, initialize);
		return list;
	}

	public boolean accept(Class<?> e) {
		if (!ReflectionUtils.isPresent(e)) {
			return false;
		}

		if (e.getAnnotation(Deprecated.class) != null || e.getAnnotation(Ignore.class) != null) {
			return false;
		}

		UseJavaVersion useJavaVersion = e.getAnnotation(UseJavaVersion.class);
		// 如果java主版本号小于要求的版本号那么忽略该类
		if (useJavaVersion != null && JavaVersion.INSTANCE.getMasterVersion() < useJavaVersion.value()) {
			return false;
		}

		return true;
	}

	private Class<?> forFileName(String classFile, ClassLoader classLoader, boolean initialize) {
		if (!classFile.endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
			return null;
		}

		String name = classFile.substring(0, classFile.length() - 6);
		name = name.replaceAll("\\\\", ".");
		name = name.replaceAll("/", ".");
		try {
			Class<?> clazz = ClassUtils.forName(name, initialize, classLoader);
			if (accept(clazz)) {
				return clazz;
			}
		} catch (Throwable e) {
		}
		return null;
	}

	private void appendDirectoryClass(String rootPackage, File file, Collection<Class<?>> classList,
			ClassLoader classLoader, boolean initialize) {
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				appendDirectoryClass(
						StringUtils.isEmpty(rootPackage) ? f.getName() + "." : rootPackage + f.getName() + ".", f,
						classList, classLoader, initialize);
			} else {
				if (f.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
					String classFile = StringUtils.isEmpty(rootPackage) ? f.getName() : rootPackage + f.getName();
					Class<?> clz = forFileName(classFile, classLoader, initialize);
					if (clz != null) {
						classList.add(clz);
					}
				}
			}
		}
	}
}
