package run.soeasy.framework.core.execution.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Executor;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.ObjectUtils;

public interface Proxy extends Executor {
	@Override
	default Object execute() {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	@Override
	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args);
}
