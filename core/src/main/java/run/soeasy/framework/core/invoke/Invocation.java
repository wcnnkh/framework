package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

public interface Invocation extends Execution, Invoker {
	@FunctionalInterface
	public static interface InvocationWrapper<W extends Invocation>
			extends Invocation, ExecutionWrapper<W>, InvokerWrapper<W> {
		@Override
		default Object execute(@NonNull Object... args) throws Throwable {
			return getSource().execute(args);
		}

		@Override
		default Object getTarget() {
			return getSource().getTarget();
		}

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
		default Object invoke(Object target, @NonNull Object... args) throws Throwable {
			return getSource().invoke(target, args);
		}

		@Override
		default Object invoke(Object target, @NonNull ParameterTemplate parameters) throws Throwable {
			return getSource().invoke(target, parameters);
		}

		@Override
		default Invocation rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default void setTarget(Object target) {
			getSource().setTarget(target);
		}
	}

	public static class RenamedMethod<W extends Invocation> extends RenamedExecution<W> implements InvocationWrapper<W> {

		public RenamedMethod(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Invocation rename(String name) {
			return new RenamedMethod<>(name, getSource());
		}
	}

	@Override
	default Object execute(@NonNull Object... args) throws Throwable {
		return execute(getTarget(), args);
	}

	Object getTarget();

	default Object invoke(Object target) throws Throwable {
		return invoke(target, ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return invoke(target, args);
	}

	Object invoke(Object target, @NonNull Object... args) throws Throwable;

	default Object invoke(Object target, @NonNull ParameterTemplate parameters) throws Throwable {
		return invoke(target, parameters.getTypes(), parameters.getArgs());
	}

	@Override
	default Invocation rename(String name) {
		return new RenamedMethod<>(name, this);
	}

	void setTarget(Object target);
}
