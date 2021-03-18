package scw.context.support;

import java.io.IOException;

import scw.context.ClassesLoader;
import scw.context.ClassesLoaderFactory;
import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.filter.TypeFilter;
import scw.core.type.scanner.DefaultClassScanner;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.util.Accept;
import scw.util.ClassLoaderProvider;
import scw.util.ConcurrentReferenceHashMap;

public class DefaultClassesLoaderFactory extends DefaultClassScanner implements
		ClassesLoaderFactory, TypeFilter {
	private ConcurrentReferenceHashMap<String, ClassesLoader<?>> cacheMap;
	private ClassLoaderProvider classLoaderProvider;
	
	public DefaultClassesLoaderFactory(boolean cache) {
		if(cache){
			cacheMap = new ConcurrentReferenceHashMap<String, ClassesLoader<?>>();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ClassesLoader<?> getClassesLoaderInternal(final String packageName){
		if(cacheMap == null){
			return new ClassScannerClassesLoader(this, this, packageName, this);
		}else{
			ClassesLoader<?> classesLoader = cacheMap.get(packageName);
			if(classesLoader != null){
				return classesLoader;
			}
			
			String[] parentPackageNames = ClassUtils.getParentPackageNames(packageName);
			if (parentPackageNames.length != 0) {
				for (int len = parentPackageNames.length, i = len - 1; i >= 0; i--) {
					ClassesLoader<?> classes = cacheMap.get(parentPackageNames[i]);
					if(classes == null){
						continue;
					}
					
					return new AcceptClassesLoader(classes, new Accept<Class<?>>() {
						public boolean accept(Class<?> e) {
							return e.getName().startsWith(packageName);
						}
					}, false);
				}
			}
			
			classesLoader = new ClassScannerClassesLoader(this, this, packageName, this);
			ClassesLoader<?> cache = cacheMap.putIfAbsent(packageName, classesLoader);
			if(cache != null){
				classesLoader = cache;
			}
			return classesLoader;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ClassesLoader<?> getClassesLoader(final String packageName) {
		String[] packageNames = StringUtils.commonSplit(packageName);
		DefaultClassesLoader<?> editableClassesLoader = new DefaultClassesLoader();
		for(String name : packageNames){
			editableClassesLoader.add((ClassesLoader)getClassesLoaderInternal(name));
		}
		return editableClassesLoader;
	}

	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {
		return true;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
