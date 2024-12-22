package io.basc.framework.core.execution;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.util.ClassUtils;
import lombok.NonNull;

public interface Executed extends AnnotatedTypeMetadata {
	@FunctionalInterface
	public static interface ExecutedWrapper<W extends Executed> extends Executed, AnnotatedTypeMetadataWrapper<W> {
		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
		}

		@Override
		default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
			return getSource().canExecuted(parameterTypes);
		}

		@Override
		default boolean canExecuted(@NonNull Parameters parameters) {
			return getSource().canExecuted(parameters);
		}

		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}
	}

	default boolean canExecuted() {
		return canExecuted(ClassUtils.emptyArray());
	}

	boolean canExecuted(@NonNull Class<?>... parameterTypes);

	default boolean canExecuted(@NonNull Parameters parameters) {
		return parameters.isValidated() && canExecuted(parameters.getTypes());
	}

	/**
	 * 返回类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getReturnTypeDescriptor();
}
