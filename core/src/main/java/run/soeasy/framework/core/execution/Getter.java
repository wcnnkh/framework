package run.soeasy.framework.core.execution;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.AnnotatedElementWrapper;
import run.soeasy.framework.core.MergedAnnotatedElement;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyDescriptor;
import run.soeasy.framework.core.param.ParameterDescriptor;
import run.soeasy.framework.util.collections.Elements;

public interface Getter extends Executable, PropertyDescriptor {

	@FunctionalInterface
	public static interface GetterWrapper<W extends Getter>
			extends Getter, ExecutableWrapper<W>, PropertyDescriptorWrapper<W> {
		@Override
		default Object get(Object target) {
			return getSource().get(target);
		}

		@Override
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}

		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}

		@Override
		default Getter rename(String name) {
			return getSource().rename(name);
		}
	}

	public class MergedGetter<E extends Getter> extends MergedPropertyDescriptor<E>
			implements Getter, AnnotatedElementWrapper<AnnotatedElement> {

		public MergedGetter(Elements<? extends E> elements) {
			super(elements);
		}

		public MergedGetter(MergedPropertyDescriptor<E> mergedPropertyDescriptor) {
			super(mergedPropertyDescriptor);
		}

		@Override
		public Object get(Object source) {
			return getMaster().get(source);
		}

		@Override
		public TypeDescriptor getDeclaringTypeDescriptor() {
			return getMaster().getDeclaringTypeDescriptor();
		}

		@Override
		public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
			return getMaster().getExceptionTypeDescriptors();
		}

		@Override
		public MergedGetter<E> rename(String name) {
			MergedPropertyDescriptor<E> mergedPropertyDescriptor = super.rename(name);
			return new MergedGetter<>(mergedPropertyDescriptor);
		}

		@Override
		public AnnotatedElement getSource() {
			return new MergedAnnotatedElement(getElements());
		}
	}

	public static class RenamedGetter<W extends Getter> extends RenamedExecutable<W> implements GetterWrapper<W> {

		public RenamedGetter(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Getter rename(String name) {
			return new RenamedGetter<>(name, getSource());
		}
	}

	Object get(Object target);

	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.empty();
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	default Getter rename(String name) {
		return new RenamedGetter<>(name, this);
	}
}
