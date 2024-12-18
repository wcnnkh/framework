package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface Property extends Access, PropertyDescriptor {

	@FunctionalInterface
	public static interface PropertyWrapper<W extends Property> extends Property, AccessWrapper<W> {

		@Override
		W getSource();

		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

		@Override
		default String getName() {
			return getSource().getName();
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class SimpleProperty extends SimplePropertyDescriptor implements Property {
		private static final long serialVersionUID = 1L;
		private Object value;

		public SimpleProperty(@NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name, typeDescriptor);
		}

		@Override
		public Object get() throws ConversionException {
			return value;
		}

		@Override
		public void set(Object source) throws UnsupportedOperationException {
			this.value = source;
		}
	}

	public static Property of(@NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
		return new SimpleProperty(name, typeDescriptor);
	}

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return Access.super.getTypeDescriptor();
	}
}
