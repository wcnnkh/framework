
package run.soeasy.framework.core.convert.property;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public interface PropertyAccessor extends TypedValueAccessor, PropertyDescriptor {

	public static interface PropertyAccessorWrapper<W extends PropertyAccessor>
			extends PropertyAccessor, TypedValueAccessorWrapper<W>, PropertyDescriptorWrapper<W> {
		@Override
		default PropertyAccessor rename(String name) {
			return PropertyAccessor.super.rename(name);
		}
	}

	public static class RenamedPropertyAccessor<W extends PropertyAccessor> extends NamedPropertyDescriptor<W>
			implements PropertyAccessorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public RenamedPropertyAccessor(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public PropertyAccessor rename(String name) {
			return new RenamedPropertyAccessor<>(getSource(), name);
		}
	}

	@Override
	default PropertyAccessor rename(String name) {
		return new RenamedPropertyAccessor<>(this, name);
	}

	public static PropertyAccessor of(PropertyDescriptor propertyDescriptor) {
		return new ConvertingPropertyAccessor<PropertyDescriptor>(propertyDescriptor);
	}
	
}
