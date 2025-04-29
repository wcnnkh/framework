package run.soeasy.framework.core.invoke;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.mapping.ParameterDescriptor;
import run.soeasy.framework.core.transform.mapping.ParameterDescriptors;

/**
 * 可执行的描述
 * 
 * @author wcnnkh
 *
 */
public interface ExecutableDescriptor extends Executable, Named {
	@FunctionalInterface
	public static interface ExecutableDescriptorWrapper<W extends ExecutableDescriptor>
			extends ExecutableDescriptor, ExecutableWrapper<W>, NamedWrapper<W> {

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
		default int getModifiers() {
			return getSource().getModifiers();
		}

		@Override
		default ExecutableDescriptor rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default ParameterDescriptors<? extends ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}
	}

	@Data
	public static class RenamedExecutable<W extends ExecutableDescriptor> implements ExecutableDescriptorWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;

		@Override
		public ExecutableDescriptor rename(String name) {
			return new RenamedExecutable<>(name, source);
		}
	}

	@Override
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		Iterator<? extends ParameterDescriptor> iterator1 = getParameterDescriptors().iterator();
		Iterator<Class<?>> iterator2 = Arrays.asList(parameterTypes).iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			ParameterDescriptor parameterDescriptor = iterator1.next();
			Class<?> type = iterator2.next();
			if (!type.isAssignableFrom(parameterDescriptor.getReturnTypeDescriptor().getType())) {
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

	ParameterDescriptors<? extends ParameterDescriptor> getParameterDescriptors();

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
	default ExecutableDescriptor rename(@NonNull String name) {
		return new RenamedExecutable<>(name, this);
	}
}
