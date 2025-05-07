package run.soeasy.framework.core.convert.property;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.convert.AccessibleDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface PropertyDescriptor extends Named, AccessibleDescriptor {

	@FunctionalInterface
	public static interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
			extends PropertyDescriptor, NamedWrapper<W>, AccessibleDescriptorWrapper<W> {

		@Override
		default PropertyDescriptor rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class NamedPropertyDescriptor<W extends AccessibleDescriptor>
			implements PropertyDescriptor, AccessibleDescriptorWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private final W source;
		private final String name;

		@Override
		public PropertyDescriptor rename(String name) {
			return new NamedPropertyDescriptor<>(source, name);
		}
	}

	@Override
	default PropertyDescriptor rename(String name) {
		return new NamedPropertyDescriptor<>(this, name);
	}

	public static PropertyDescriptor forAccessibleDescriptor(String name, AccessibleDescriptor accessibleDescriptor) {
		return new NamedPropertyDescriptor<>(accessibleDescriptor, name);
	}

	public static PropertyDescriptor forTypeDescriptor(String name, TypeDescriptor typeDescriptor) {
		SimplePropertyDescriptor propertyDescriptor = new SimplePropertyDescriptor(typeDescriptor);
		propertyDescriptor.setName(name);
		return propertyDescriptor;
	}
}
