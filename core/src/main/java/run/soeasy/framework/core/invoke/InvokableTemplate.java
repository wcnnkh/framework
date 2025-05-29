package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ClassUtils;
import run.soeasy.framework.core.ObjectUtils;

public interface InvokableTemplate extends ExecutableDescriptor {
	default Object invoke(Object target) throws Throwable {
		return invoke(target, ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;

}
