
package run.soeasy.framework.core.convert.mapping;

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

	public static class RenamedPropertyAccessor<W extends PropertyAccessor> extends RenamedPropertyDescriptor<W>
			implements PropertyAccessorWrapper<W> {

		public RenamedPropertyAccessor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public PropertyAccessor rename(String name) {
			return new RenamedPropertyAccessor<>(name, getSource());
		}
	}

	@Override
	default PropertyAccessor rename(String name) {
		return new RenamedPropertyAccessor<>(name, this);
	}
}
