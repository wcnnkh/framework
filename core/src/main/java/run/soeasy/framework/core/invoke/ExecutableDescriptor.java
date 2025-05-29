package run.soeasy.framework.core.invoke;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.ClassUtils;
import run.soeasy.framework.core.convert.value.SourceDescriptor;

/**
 * 可执行器的描述
 * 
 * @author wcnnkh
 *
 */
public interface ExecutableDescriptor extends SourceDescriptor, AnnotatedElement {
	default boolean canExecuted() {
		return canExecuted(ClassUtils.emptyArray());
	}

	boolean canExecuted(@NonNull Class<?>... parameterTypes);
}
