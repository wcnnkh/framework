package run.soeasy.framework.core.invoke;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.convert.Readable;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.mapping.ParameterSource;
import run.soeasy.framework.core.type.ClassUtils;

public interface Executable extends Readable, AnnotatedElement {
	@FunctionalInterface
	public static interface ExecutableWrapper<W extends Executable>
			extends Executable, ReadableWrapper<W>, AnnotatedElementWrapper<W> {

		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
		}

		@Override
		default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
			return getSource().canExecuted(parameterTypes);
		}

		@Override
		default boolean canExecuted(@NonNull ParameterSource parameters) {
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

	default boolean canExecuted(@NonNull ParameterSource parameters) {
		return parameters.isValidated() && canExecuted(parameters.getTypes());
	}
}
