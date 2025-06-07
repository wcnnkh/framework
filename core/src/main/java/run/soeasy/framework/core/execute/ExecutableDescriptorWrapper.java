package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptorWrapper;

@FunctionalInterface
public interface ExecutableDescriptorWrapper<W extends ExecutableDescriptor>
		extends ExecutableDescriptor, SourceDescriptorWrapper<W>, AnnotatedElementWrapper<W> {

	@Override
	default boolean canExecuted() {
		return getSource().canExecuted();
	}

	@Override
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return getSource().canExecuted(parameterTypes);
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return getSource().getReturnTypeDescriptor();
	}
}