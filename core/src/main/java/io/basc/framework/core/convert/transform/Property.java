
package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.Value;
import lombok.Data;
import lombok.NonNull;

public interface Property extends Access, PropertyDescriptor {

	public static interface PropertyWrapper<W extends Property>
			extends Property, AccessWrapper<W>, PropertyDescriptorWrapper<W> {
	}

	public static class SharedProperty<W extends PropertyDescriptor> extends SharedAccess<W>
			implements Property, PropertyDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public SharedProperty(@NonNull W source) {
			super(source);
		}
	}

	@Data
	public static class StandardProperty<W extends Access> implements Property, AccessWrapper<W> {
		private final String name;
		@NonNull
		private final W source;
	}

	public static Property of(String name, @NonNull AccessDescriptor accessDescriptor) {
		return new SharedProperty<>(PropertyDescriptor.of(name, accessDescriptor));
	}

	public static Property of(String name, @NonNull Value value) {
		Access access = Access.of(value);
		return new StandardProperty<>(name, access);
	}
}
