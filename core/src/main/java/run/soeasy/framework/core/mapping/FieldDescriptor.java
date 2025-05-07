package run.soeasy.framework.core.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.property.PropertyDescriptor;
import run.soeasy.framework.core.invoke.mapping.Getter;
import run.soeasy.framework.core.invoke.mapping.Setter;

public interface FieldDescriptor extends PropertyDescriptor {
	@FunctionalInterface
	public static interface FieldDescriptorWrapper<W extends FieldDescriptor>
			extends FieldDescriptor, PropertyDescriptorWrapper<W> {
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
		default FieldDescriptor rename(String name) {
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

	public static class RenamedFieldDescriptor<W extends FieldDescriptor> extends RenamedPropertyDescriptor<W>
			implements FieldDescriptorWrapper<W> {

		public RenamedFieldDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public FieldDescriptor rename(String name) {
			return new RenamedFieldDescriptor<>(name, getSource());
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
	default FieldDescriptor rename(String name) {
		return new RenamedFieldDescriptor<>(name, this);
	}
}
