
package run.soeasy.framework.core.transform.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.value.Accessible;
import run.soeasy.framework.core.convert.value.Accessor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public interface Property extends ValueAccessor, PropertyDescriptor {

	public static interface PropertyWrapper<W extends Property>
			extends Property, ValueAccessorWrapper<W>, PropertyDescriptorWrapper<W> {
		@Override
		default Property rename(String name) {
			return Property.super.rename(name);
		}
	}

	public static class StandardProperty<W extends PropertyDescriptor> extends StandardAccess<W>
			implements Property, PropertyDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public StandardProperty(@NonNull W source) {
			super(source);
		}

		@Override
		public Property rename(String name) {
			return Property.super.rename(name);
		}
	}

	@Data
	public static class PropertyAccessor<W extends Accessor> implements Property, AccessWrapper<W> {
		private final String name;
		@NonNull
		private final W source;
	}

	public static Property of(String name, @NonNull Accessible accessDescriptor) {
		return new StandardProperty<>(PropertyDescriptor.of(name, accessDescriptor));
	}

	public static Property of(String name, @NonNull ValueAccessor value) {
		Accessor access = Accessor.of(value);
		return new PropertyAccessor<>(name, access);
	}

	@Override
	default Property rename(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
