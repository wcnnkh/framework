package run.soeasy.framework.core.reflect;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

public interface ReflectionFactory<T> {
	Provider<T> getReflectionProvider(@NonNull Class<?> declaringClass);
}
