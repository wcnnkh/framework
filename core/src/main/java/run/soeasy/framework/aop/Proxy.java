package run.soeasy.framework.aop;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.ClassUtils;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.invoke.ExecutableTemplate;

public interface Proxy extends ExecutableTemplate, AnnotatedElementWrapper<AnnotatedElement> {
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
