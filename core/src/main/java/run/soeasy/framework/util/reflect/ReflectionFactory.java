package run.soeasy.framework.util.reflect;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;

public interface ReflectionFactory<T> {
	Provider<T> getReflectionProvider(@NonNull Class<?> declaringClass);
}
