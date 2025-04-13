package run.soeasy.framework.core.execution;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

public interface Invoker extends Executed {
	@FunctionalInterface
	public static interface InvokerWrapper<W extends Invoker> extends Invoker, ExecutedWrapper<W> {
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
		default Object invoke(Object target, @NonNull Parameters parameters) throws Throwable {
			return getSource().invoke(target, parameters);
		}

	}

	default Object invoke(Object target) throws Throwable {
		return invoke(target, ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;

	default Object invoke(Object target, @NonNull Parameters parameters) throws Throwable {
		if (!parameters.isValidated()) {
			throw new IllegalArgumentException();
		}
		return invoke(target, parameters.getTypes(), parameters.getArgs());
	}
}
