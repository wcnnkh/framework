package run.soeasy.framework.core.exe;

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
 * 所有执行的基类
 * 
 * @author wcnnkh
 *
 */
public interface Executable extends Executed, Named {
	@FunctionalInterface
	public static interface ExecutableWrapper<W extends Executable>
			extends Executable, ExecutedWrapper<W>, NamedWrapper<W> {

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
		default Executable rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default ParameterDescriptors<? extends ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
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
		Iterator<? extends ParameterDescriptor> iterator1 = getParameterDescriptors().iterator();
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
	default Executable rename(@NonNull String name) {
		return new RenamedExecutable<>(name, this);
	}
}
