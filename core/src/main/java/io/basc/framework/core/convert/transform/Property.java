package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface Property extends Access, PropertyDescriptor {
	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class SimpleProperty extends SimplePropertyDescriptor implements Property {
		private static final long serialVersionUID = 1L;
		private Object source;

		public SimpleProperty(@NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name, typeDescriptor);
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
