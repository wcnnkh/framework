package io.basc.framework.core.execution;

import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.util.Elements;
import lombok.NonNull;

/**
 * 方法的定义
 * 
 * @author wcnnkh
 *
 */
public interface Method extends Function, Invoker {
	@Override
	default Object execute(@NonNull Object... args) throws Throwable {
		return execute(getTarget(), args);
	}

	default Object invoke(Object target) throws Throwable {
		return execute(target, Elements.empty(), Elements.empty());
	}

	default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return invoke(target, args);
	}

	Object invoke(Object target, @NonNull Object... args) throws Throwable;

	default Object invoke(Object target, @NonNull Parameters parameters) throws Throwable {
		return execute(target, parameters.getTypes(), parameters.getArgs());
	}

	Object getTarget();

	void setTarget(Object target);
}
