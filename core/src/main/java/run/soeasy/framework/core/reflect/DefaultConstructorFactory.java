package run.soeasy.framework.core.reflect;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultConstructorFactory extends ConstructorFactory {
	@NonNull
	private final Function<? super Class<?>, ? extends Constructor<?>[]> loader;

	@Override
	protected Constructor<?>[] loadConstructors(Class<?> declaringClass) {
		return loader.apply(declaringClass);
	}

}
