package run.soeasy.framework.core.execution;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.param.Parameters;
import run.soeasy.framework.lang.AnnotatedElementWrapper;
import run.soeasy.framework.util.ClassUtils;

public interface Executed extends AnnotatedElement {
	@FunctionalInterface
	public static interface ExecutedWrapper<W extends Executed> extends Executed, AnnotatedElementWrapper<W> {

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
