package io.basc.framework.core.execution.stereotype;

import io.basc.framework.core.convert.transform.PropertyDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.core.execution.Setter;
import lombok.NonNull;

public interface PropertyAccessDescriptor extends PropertyDescriptor {
	@FunctionalInterface
	public static interface AccessDescriptorWrapper<W extends PropertyAccessDescriptor>
			extends PropertyAccessDescriptor, PropertyDescriptorWrapper<W> {
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
		default boolean isWritable() {
			return getSource().isWritable();
		}

		@Override
		default PropertyAccessDescriptor rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedAccessDescriptor<W extends PropertyAccessDescriptor> extends RenamedPropertyDescriptor<W>
			implements AccessDescriptorWrapper<W> {

		public RenamedAccessDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public PropertyAccessDescriptor rename(String name) {
			return new RenamedAccessDescriptor<>(name, getSource());
		}
	}

	Getter getReadMethod();

	Setter getWriteMethod();

	boolean isReadable();

	boolean isWritable();

	@Override
	default PropertyAccessDescriptor rename(String name) {
		return new RenamedAccessDescriptor<>(name, this);
	}
}
