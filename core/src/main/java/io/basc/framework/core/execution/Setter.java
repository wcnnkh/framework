package io.basc.framework.core.execution;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.PropertyDescriptor;
import io.basc.framework.util.collection.Elements;
import lombok.NonNull;

public interface Setter extends Executable, PropertyDescriptor {

	public class MergedSetter<E extends Setter> extends MergedPropertyDescriptor<E> implements Setter {

		public MergedSetter(Elements<? extends E> elements) {
			super(elements);
		}

		public MergedSetter(MergedPropertyDescriptor<E> mergedPropertyDescriptor) {
			super(mergedPropertyDescriptor);
		}

		@Override
		public TypeDescriptor getDeclaringTypeDescriptor() {
			return getMaster().getReturnTypeDescriptor();
		}

		@Override
		public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
			return getMaster().getExceptionTypeDescriptors();
		}

		@Override
		public MergedSetter<E> rename(String name) {
			MergedPropertyDescriptor<E> mergedPropertyDescriptor = super.rename(name);
			return new MergedSetter<>(mergedPropertyDescriptor);
		}

		@Override
		public void set(Object target, Object value) {
			getMaster().set(target, value);
		}
	}

	public static class RenamedSetter<W extends Setter> extends RenamedExecutable<W> implements SetterWrapper<W> {

		public RenamedSetter(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Setter rename(String name) {
			return new RenamedSetter<>(name, getSource());
		}
	}

	@FunctionalInterface
	public static interface SetterWrapper<W extends Setter>
			extends Setter, ExecutableWrapper<W>, PropertyDescriptorWrapper<W> {
		@Override
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}

		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}

		@Override
		default Setter rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default void set(Object target, Object value) {
			getSource().set(target, value);
		}
	}

	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.singleton(ParameterDescriptor.of(0, this));
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return TypeDescriptor.valueOf(Void.class);
	}

	@Override
	default Setter rename(String name) {
		return new RenamedSetter<>(name, this);
	}

	void set(Object target, Object value);
}
