package scw.core.utils;

import java.util.Map;
import java.util.Set;

import scw.util.ConcurrentReferenceHashMap;

public class CacheScanningPackage extends ScanningPackage{
	private Map<String, Set<Class<?>>> classCache = new ConcurrentReferenceHashMap<String, Set<Class<?>>>();
	
	@Override
	public Set<Class<?>> getClassList(String packageName,
			ClassLoader classLoader, boolean initialize) {
		Set<Class<?>> set = classCache.get(packageName);
		if(set == null){
			set = super.getClassList(packageName, classLoader, initialize);
			classCache.putIfAbsent(packageName, set);
		}
		return set;
	}
}
