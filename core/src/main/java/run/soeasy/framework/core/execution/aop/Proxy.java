package run.soeasy.framework.core.execution.aop;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Executor;
import run.soeasy.framework.lang.AnnotatedElementWrapper;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.ObjectUtils;

public interface Proxy extends Executor, AnnotatedElementWrapper<AnnotatedElement> {
	@Override
	default Object execute() {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	@Override
	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args);

	@Override
	default AnnotatedElement getSource() {
		return getReturnTypeDescriptor();
	}
}
