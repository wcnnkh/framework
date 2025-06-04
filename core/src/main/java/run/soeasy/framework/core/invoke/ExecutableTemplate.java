package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

public interface ExecutableTemplate extends ExecutableDescriptor {
	default Object execute() throws Throwable {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;
}