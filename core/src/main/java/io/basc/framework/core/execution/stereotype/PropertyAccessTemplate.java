package io.basc.framework.core.execution.stereotype;

import io.basc.framework.core.convert.transform.PropertyDescriptors;
import io.basc.framework.util.alias.Named;
import lombok.Data;
import lombok.NonNull;

public interface PropertyAccessTemplate<T extends PropertyAccessDescriptor> extends PropertyDescriptors<T>, Named {
	@FunctionalInterface
	public static interface PropertyAccessTemplateWrapper<T extends PropertyAccessDescriptor, W extends PropertyAccessTemplate<T>>
			extends PropertyAccessTemplate<T>, PropertyDescriptorsWrapper<T, W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default PropertyAccessTemplate<T> rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class RenamedPropertyAccessTemplate<T extends PropertyAccessDescriptor, W extends PropertyAccessTemplate<T>>
			implements PropertyAccessTemplateWrapper<T, W> {
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
		public PropertyAccessTemplate<T> rename(String name) {
			return new RenamedPropertyAccessTemplate<>(name, source);
		}
	}

	/**
	 * 模板名称
	 */
	@Override
	String getName();

	@Override
	default PropertyAccessTemplate<T> rename(String name) {
		return new RenamedPropertyAccessTemplate<>(name, this);
	}
}
