package run.soeasy.framework.core.execution;

import lombok.NonNull;
import run.soeasy.framework.core.param.Parameters;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.ObjectUtils;

/**
 * 方法的定义
 * 
 * @author wcnnkh
 *
 */
public interface Method extends Function, Invoker {
	@FunctionalInterface
	public static interface MethodWrapper<W extends Method> extends Method, FunctionWrapper<W>, InvokerWrapper<W> {
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
		default Object invoke(Object target, @NonNull Parameters parameters) throws Throwable {
			return getSource().invoke(target, parameters);
		}

		@Override
		default Method rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default void setTarget(Object target) {
			getSource().setTarget(target);
		}
	}

	public static class RenamedMethod<W extends Method> extends RenamedFunction<W> implements MethodWrapper<W> {

		public RenamedMethod(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Method rename(String name) {
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

	default Object invoke(Object target, @NonNull Parameters parameters) throws Throwable {
		Parameters completed = parameters.isValidated() ? parameters : parameters.reconstruct(this);
		return invoke(target, completed.getTypes(), completed.getArgs());
	}

	@Override
	default Method rename(String name) {
		return new RenamedMethod<>(name, this);
	}

	void setTarget(Object target);
}
