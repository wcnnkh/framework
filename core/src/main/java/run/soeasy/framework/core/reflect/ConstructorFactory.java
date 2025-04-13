package run.soeasy.framework.core.reflect;

import java.lang.reflect.Constructor;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ConcurrentReferenceHashMap;
import run.soeasy.framework.core.collection.Provider;

public abstract class ConstructorFactory implements ReflectionFactory<Constructor<?>> {
	public static final Constructor<?>[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor<?>[0];

	private final ConcurrentReferenceHashMap<Class<?>, Provider<Constructor<?>>> cacheMap = new ConcurrentReferenceHashMap<>();

	@Override
	public Provider<Constructor<?>> getReflectionProvider(@NonNull Class<?> declaringClass) {
		Provider<Constructor<?>> provider = cacheMap.get(declaringClass);
		if (provider == null) {
			provider = new ReflectionProvider<>(declaringClass, this::loadConstructors);
			Provider<Constructor<?>> old = cacheMap.putIfAbsent(declaringClass, provider);
			if (old == null) {
				// 插入成功，整理内存
				cacheMap.purgeUnreferencedEntries();
			} else {
				provider = old;
			}
		}
		return provider;
	}

	protected abstract Constructor<?>[] loadConstructors(Class<?> declaringClass);
}
