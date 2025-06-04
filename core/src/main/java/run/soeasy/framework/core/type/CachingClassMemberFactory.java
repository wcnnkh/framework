package run.soeasy.framework.core.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.ConcurrentReferenceHashMap;
import run.soeasy.framework.core.collection.Provider;

@RequiredArgsConstructor
@Getter
public class CachingClassMemberFactory<T> implements ClassMemberFactory<T> {
	private final ConcurrentReferenceHashMap<Class<?>, Provider<T>> cacheMap = new ConcurrentReferenceHashMap<>();
	@NonNull
	private final ClassMemberFactory<T> classMemberFactory;

	@Override
	public Provider<T> getClassMemberProvider(@NonNull Class<?> declaringClass) {
		Provider<T> provider = cacheMap.get(declaringClass);
		if (provider == null) {
			provider = classMemberFactory.getClassMemberProvider(declaringClass);
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
