package io.basc.framework.core.execution;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.alias.Named;
import io.basc.framework.util.collection.Elements;
import lombok.Data;
import lombok.NonNull;

/**
 * 所有执行的基类
 * 
 * @author wcnnkh
 *
 */
public interface Executable extends Executed, Named, ParameterDescriptorTemplate {
	@FunctionalInterface
	public static interface ExecutableWrapper<W extends Executable>
			extends Executable, ExecutedWrapper<W>, NamedWrapper<W>, ParameterDescriptorTemplateWrapper<W> {
		@Override
		default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
			return getSource().canExecuted(parameterTypes);
		}

		@Override
		default TypeDescriptor getDeclaringTypeDescriptor() {
			return getSource().getDeclaringTypeDescriptor();
		}

		@Override
		default boolean canExecuted(@NonNull Parameters parameters) {
			return getSource().canExecuted(parameters);
		}

		@Override
		default Elements<TypeDescriptor> getExceptionTypeDescriptors() {
			return getSource().getExceptionTypeDescriptors();
		}

		@Override
		default int getModifiers() {
			return getSource().getModifiers();
		}

		@Override
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}

		@Override
		default Executable rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class RenamedExecutable<W extends Executable> implements ExecutableWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;

		@Override
		public Executable rename(String name) {
			return new RenamedExecutable<>(name, source);
		}
	}

	@Override
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		Iterator<ParameterDescriptor> iterator1 = getParameterDescriptors().iterator();
		Iterator<Class<?>> iterator2 = Arrays.asList(parameterTypes).iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			ParameterDescriptor parameterDescriptor = iterator1.next();
			Class<?> type = iterator2.next();
			if (!type.isAssignableFrom(parameterDescriptor.getTypeDescriptor().getType())) {
				return false;
			}
		}
		return !iterator1.hasNext() && !iterator2.hasNext();
	}

	/**
	 * 声明的类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getDeclaringTypeDescriptor();

	/**
	 * 
	 * 异常类型
	 * 
	 * @return
	 */
	Elements<TypeDescriptor> getExceptionTypeDescriptors();

	/**
	 * Returns the Java language modifiers for the member or constructor represented
	 * by this Member, as an integer. The Modifier class should be used to decode
	 * the modifiers in the integer.
	 *
	 * @return the Java language modifiers for the underlying member
	 * @see Modifier
	 */
	default int getModifiers() {
		return Modifier.PUBLIC;
	}

	@Override
	default Executable rename(@NonNull String name) {
		return new RenamedExecutable<>(name, this);
	}

	@Override
	default boolean canExecuted(@NonNull Parameters parameters) {
		if (parameters.isValidated()) {
			return Executed.super.canExecuted(parameters);
		} else {
			if (parameters.test(this)) {
				return Executed.super.canExecuted(parameters.reconstruct(this));
			}
		}
		return false;
	}
}
