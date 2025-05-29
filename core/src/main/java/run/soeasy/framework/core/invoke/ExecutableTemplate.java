package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ClassUtils;
import run.soeasy.framework.core.ObjectUtils;

public interface ExecutableTemplate extends ExecutableDescriptor {
	default Object execute() throws Throwable {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;
}