package run.soeasy.framework.core.convert.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.convert.AccessibleDescriptor;

public interface PropertyDescriptor extends Named, AccessibleDescriptor {

	@FunctionalInterface
	public static interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
			extends PropertyDescriptor, NamedWrapper<W>, AccessibleDescriptorWrapper<W> {

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

	@Override
	default PropertyDescriptor rename(String name) {
		return new RenamedPropertyDescriptor<>(name, this);
	}
}
