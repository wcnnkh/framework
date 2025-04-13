package run.soeasy.framework.core.reflect;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.type.ClassUtils;

public class ExecutableProvider<T extends Executable> extends MemberProvider<T> {
	private static final long serialVersionUID = 1L;

	public ExecutableProvider(@NonNull Class<?> declaringClass,
			@NonNull Function<? super Class<?>, ? extends T[]> loader) {
		super(declaringClass, loader);
	}

	public Provider<T> getProvider(String name, Iterable<? extends Class<?>> parameterTypes) {
		Provider<T> provider = getProvider(name);
		return provider == null ? null
				: provider.filter((e) -> CollectionUtils.equals(Arrays.asList(e.getParameterTypes()), parameterTypes,
						ClassUtils::isAssignable));
	}

	public final Provider<T> getProvider(String name, Class<?>... parameterTypes) {
		return getProvider(name, Arrays.asList(parameterTypes));
	}
}
