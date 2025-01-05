package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.core.execution.Setter;
import io.basc.framework.core.mapping.PropertyDescriptor;
import lombok.NonNull;

public interface FieldDescriptor extends PropertyDescriptor {
	@FunctionalInterface
	public static interface FieldDescriptorWrapper<W extends FieldDescriptor>
			extends FieldDescriptor, PropertyDescriptorWrapper<W> {
		@Override
		default Getter getReadMethod() {
			return getSource().getReadMethod();
		}

		@Override
		default Setter getWriteMethod() {
			return getSource().getWriteMethod();
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
			return getReadMethod().getTypeDescriptor();
		}
		throw new UnsupportedOperationException();
	}

	@Override
	default TypeDescriptor getRequiredTypeDescriptor() {
		if (isWritable()) {
			return getWriteMethod().getTypeDescriptor();
		}
		throw new UnsupportedOperationException();
	}

	Getter getReadMethod();

	Setter getWriteMethod();

	boolean isReadable();

	boolean isWritable();

	@Override
	default FieldDescriptor rename(String name) {
		return new RenamedFieldDescriptor<>(name, this);
	}
}
