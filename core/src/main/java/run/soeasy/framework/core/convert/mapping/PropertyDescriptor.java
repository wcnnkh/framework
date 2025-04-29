package run.soeasy.framework.core.convert.mapping;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.convert.Accessor;

public interface PropertyDescriptor extends Named, Accessor {

	@FunctionalInterface
	public static interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
			extends PropertyDescriptor, NamedWrapper<W>, AccessibleWrapper<W> {

		@Override
		default PropertyDescriptor rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedPropertyDescriptor<W extends PropertyDescriptor> extends Renamed<W>
			implements PropertyDescriptorWrapper<W> {

		public RenamedPropertyDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public PropertyDescriptor rename(String name) {
			return new RenamedPropertyDescriptor<>(name, getSource());
		}
	}

	@Data
	public static class SharedPropertyDescriptor<W extends Accessor>
			implements PropertyDescriptor, AccessibleWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		private String name;
		@NonNull
		private final W source;

		public SharedPropertyDescriptor(@NonNull W source) {
			this.source = source;
		}
	}

	public static PropertyDescriptor of(String name, @NonNull Accessor accessDescriptor) {
		if (accessDescriptor instanceof PropertyDescriptor) {
			PropertyDescriptor propertyDescriptor = (PropertyDescriptor) accessDescriptor;
			if (StringUtils.equals(name, propertyDescriptor.getName())) {
				return propertyDescriptor;
			}
			return propertyDescriptor.rename(name);
		}

		SharedPropertyDescriptor<Accessor> shared = new SharedPropertyDescriptor<>(accessDescriptor);
		shared.setName(name);
		return shared;
	}

	@Override
	default PropertyDescriptor rename(String name) {
		return new RenamedPropertyDescriptor<>(name, this);
	}
}
