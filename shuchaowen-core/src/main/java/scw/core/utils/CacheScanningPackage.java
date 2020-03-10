package scw.core.utils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import scw.util.ConcurrentReferenceHashMap;

public class CacheScanningPackage extends ScanningPackage{
	private Map<String, Set<Class<?>>> classCache = new ConcurrentReferenceHashMap<String, Set<Class<?>>>();
	
	@Override
	public Set<Class<?>> getClassList(String packageName,
			ClassLoader classLoader, boolean initialize) {
		Set<Class<?>> set = getClassListByCache(packageName);
		if(set == null){
			String[] parentPackageNames = ClassUtils.getParentPackageNames(packageName);
			boolean sann = true;
			if(parentPackageNames.length != 0){
				for(int len = parentPackageNames.length, i = len -1; i>=0; i--){
					Set<Class<?>> tempSet = getClassListByCache(parentPackageNames[i]);
					if(tempSet == null){
						continue;
					}
					
					sann = false;
					set = getSubSet(tempSet, packageName);
					break;
				}
			}
			
			if(sann){
				set = super.getClassList(packageName, classLoader, initialize);
				classCache.putIfAbsent(packageName, set);
			}
		}
		return set;
	}
	
	private final Set<Class<?>> getClassListByCache(String packageName){
		return classCache.get(packageName);
	}
	
	private final Set<Class<?>> getSubSet(Set<Class<?>> sets, String packageName){
		LinkedHashSet<Class<?>> classes = new LinkedHashSet<Class<?>>(sets.size());
		for(Class<?> clazz : sets){
			if(clazz.getName().startsWith(packageName)){
				classes.add(clazz);
			}
		}
		return classes;
	}
}
