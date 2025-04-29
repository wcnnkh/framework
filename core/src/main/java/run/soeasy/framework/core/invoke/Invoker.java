package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.transform.mapping.ParameterSource;
import run.soeasy.framework.core.type.ClassUtils;

public interface Invoker extends Executable {
	@FunctionalInterface
	public static interface InvokerWrapper<W extends Invoker> extends Invoker, ExecutableWrapper<W> {
		@Override
		default Object invoke(Object target) throws Throwable {
			return getSource().invoke(target);
		}

		@Override
		default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args)
				throws Throwable {
			return getSource().invoke(target, parameterTypes, args);
		}

		@Override
		default Object invoke(Object target, @NonNull ParameterSource parameters) throws Throwable {
			return getSource().invoke(target, parameters);
		}

	}

	default Object invoke(Object target) throws Throwable {
		return invoke(target, ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;

	default Object invoke(Object target, @NonNull ParameterSource parameters) throws Throwable {
		if (!parameters.isValidated()) {
			throw new IllegalArgumentException();
		}
		return invoke(target, parameters.getTypes(), parameters.getArgs());
	}
}
