package run.soeasy.framework.core.execute;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 可执行器的描述
 * 
 * @author soeasy.run
 *
 */
public interface ExecutableDescriptor extends SourceDescriptor, AnnotatedElement {
	default boolean canExecuted() {
		return canExecuted(ClassUtils.emptyArray());
	}

	boolean canExecuted(@NonNull Class<?>... parameterTypes);
}
