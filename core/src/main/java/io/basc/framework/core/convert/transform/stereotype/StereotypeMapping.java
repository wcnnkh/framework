package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.transform.PropertyDescriptors;
import io.basc.framework.util.alias.Named;
import lombok.Data;
import lombok.NonNull;

public interface StereotypeMapping<T extends StereotypeDescriptor> extends PropertyDescriptors<T>, Named {
	@FunctionalInterface
	public static interface StereotypeMappingWrapper<T extends StereotypeDescriptor, W extends StereotypeMapping<T>>
			extends StereotypeMapping<T>, PropertyDescriptorsWrapper<T, W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default StereotypeMapping<T> rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class RenamedStereotypeMapping<T extends StereotypeDescriptor, W extends StereotypeMapping<T>>
			implements StereotypeMappingWrapper<T, W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public W getSource() {
			return source;
		}

		@Override
		public StereotypeMapping<T> rename(String name) {
			return new RenamedStereotypeMapping<>(name, source);
		}
	}

	/**
	 * 模板名称
	 */
	@Override
	String getName();

	@Override
	default StereotypeMapping<T> rename(String name) {
		return new RenamedStereotypeMapping<>(name, this);
	}
}
