package run.soeasy.framework.aop;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.exe.Executor;
import run.soeasy.framework.core.type.ClassUtils;

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
