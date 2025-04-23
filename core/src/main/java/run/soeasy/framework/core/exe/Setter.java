package run.soeasy.framework.core.exe;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.annotation.MergedAnnotatedElement;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.Writeable;
import run.soeasy.framework.core.transform.mapping.ParameterDescriptor;
import run.soeasy.framework.core.transform.mapping.PropertyDescriptor.MergedPropertyDescriptor;

public interface Setter extends Executable, Writeable {

	public class MergedSetter<E extends Setter> extends MergedPropertyDescriptor<E>
			implements Setter, AnnotatedElementWrapper<AnnotatedElement> {

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

		@Override
		public AnnotatedElement getSource() {
			return new MergedAnnotatedElement(getElements());
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
			extends Setter, ExecutableWrapper<W>, WriteableWrapper<W> {
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
