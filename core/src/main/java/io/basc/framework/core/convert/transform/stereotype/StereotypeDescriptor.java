package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.PropertyDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.core.execution.Setter;
import lombok.NonNull;

public interface StereotypeDescriptor extends PropertyDescriptor {
	@FunctionalInterface
	public static interface StereotypeDescriptorWrapper<W extends StereotypeDescriptor>
			extends StereotypeDescriptor, PropertyDescriptorWrapper<W> {
		@Override
		default Getter getReader() {
			return getSource().getReader();
		}

		@Override
		default Setter getWriter() {
			return getSource().getWriter();
		}

		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}

		@Override
		default boolean isWritable() {
			return getSource().isWritable();
		}

		@Override
		default StereotypeDescriptor rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default Object readFrom(Object target) throws UnsupportedOperationException {
			return getSource().readFrom(target);
		}

		@Override
		default void writeTo(Object target, Object value) throws UnsupportedOperationException {
			getSource().writeTo(target, value);
		}
	}

	public static class RenamedStereotypeDescriptor<W extends StereotypeDescriptor> extends RenamedPropertyDescriptor<W>
			implements StereotypeDescriptorWrapper<W> {

		public RenamedStereotypeDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public StereotypeDescriptor rename(String name) {
			return new RenamedStereotypeDescriptor<>(name, getSource());
		}
	}

	@Override
	default TypeDescriptor getTypeDescriptor() {
		if (isReadable()) {
			return getReader().getTypeDescriptor();
		}
		throw new UnsupportedOperationException();
	}

	@Override
	default TypeDescriptor getRequiredTypeDescriptor() {
		if (isWritable()) {
			return getWriter().getTypeDescriptor();
		}
		throw new UnsupportedOperationException();
	}

	Getter getReader();

	default Object readFrom(Object source) throws UnsupportedOperationException {
		if (!isReadable()) {
			throw new UnsupportedOperationException();
		}

		return getReader().get(source);
	}

	Setter getWriter();

	default void writeTo(Object target, Object value) throws UnsupportedOperationException {
		if (!isWritable()) {
			throw new UnsupportedOperationException();
		}

		getWriter().set(target, value);
	}

	boolean isReadable();

	boolean isWritable();

	@Override
	default StereotypeDescriptor rename(String name) {
		return new RenamedStereotypeDescriptor<>(name, this);
	}
}
