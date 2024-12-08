package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Executor;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import lombok.NonNull;

public interface Proxy extends Executor {
	@Override
	default Object execute() {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	@Override
	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args);
}
