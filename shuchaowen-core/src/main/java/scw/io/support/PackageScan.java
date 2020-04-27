package scw.io.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.classreading.SimpleMetadataReaderFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.lang.Ignore;
import scw.util.ConcurrentReferenceHashMap;

public class PackageScan {
	private final ResourcePatternResolver resourcePatternResolver;
	private final MetadataReaderFactory metadataReaderFactory;
	private boolean useCache = true;

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

	protected Collection<Class<?>> getClassesInternal(String packageName) throws IOException {
		List<Class<?>> classes = new LinkedList<Class<?>>();
		for (Resource resource : resourcePatternResolver.getResources(
				ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + packageName.replace(".", "/") + "/**/*.class")) {
			MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
			if (reader == null) {
				continue;
			}

			if (reader.getAnnotationMetadata().hasAnnotation(Deprecated.class.getName())
					|| reader.getAnnotationMetadata().hasAnnotation(Ignore.class.getName())) {
				continue;
			}

			if (reader.getClassMetadata().getClassName().startsWith("java.")
					|| reader.getClassMetadata().getClassName().startsWith("javax.")) {
				continue;
			}

			Class<?> clazz = ClassUtils.forNameNullable(reader.getClassMetadata().getClassName());
			if (clazz == null) {
				continue;
			}

			classes.add(clazz);
		}
		return classes;
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
}
