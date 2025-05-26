package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface ExecutableMetadataWrapper<W extends ExecutableMetadata>
		extends ExecutableMetadata, ExecutableDescriptorWrapper<W> {

	@Override
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return getSource().canExecuted(parameterTypes);
	}

	@Override
	default TypeDescriptor getDeclaringTypeDescriptor() {
		return getSource().getDeclaringTypeDescriptor();
	}

	@Override
	default Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return getSource().getExceptionTypeDescriptors();
	}

	@Override
	default String getName() {
		return getSource().getName();
	}

	@Override
	default ParameterTemplate getParameterTemplate() {
		return getSource().getParameterTemplate();
	}
}