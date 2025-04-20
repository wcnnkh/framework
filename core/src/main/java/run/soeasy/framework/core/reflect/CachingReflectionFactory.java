package run.soeasy.framework.core.reflect;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.ConcurrentReferenceHashMap;
import run.soeasy.framework.core.collection.Provider;

@RequiredArgsConstructor
@Getter
public class CachingReflectionFactory<T> implements ReflectionFactory<T> {
	private final ConcurrentReferenceHashMap<Class<?>, Provider<T>> cacheMap = new ConcurrentReferenceHashMap<>();
	@NonNull
	private final ReflectionFactory<T> reflectionFactory;

	@Override
	public Provider<T> getReflectionProvider(@NonNull Class<?> declaringClass) {
		Provider<T> provider = cacheMap.get(declaringClass);
		if (provider == null) {
			provider = reflectionFactory.getReflectionProvider(declaringClass);
			Provider<T> old = cacheMap.putIfAbsent(declaringClass, provider);
			if (old == null) {
				// 插入成功，整理内存
				cacheMap.purgeUnreferencedEntries();
			} else {
				provider = old;
			}
		}
		return provider;
	}
}
