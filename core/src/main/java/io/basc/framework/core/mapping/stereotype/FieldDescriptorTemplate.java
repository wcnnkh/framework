package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.mapping.PropertyDescriptors;
import io.basc.framework.util.alias.Named;
import lombok.Data;
import lombok.NonNull;

public interface FieldDescriptorTemplate<T extends FieldDescriptor> extends PropertyDescriptors<T>, Named {
	@FunctionalInterface
	public static interface FieldDescriptorTemplateWrapper<T extends FieldDescriptor, W extends FieldDescriptorTemplate<T>>
			extends FieldDescriptorTemplate<T>, PropertyDescriptorsWrapper<T, W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default FieldDescriptorTemplate<T> rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class RenamedFieldDescriptorTemplate<T extends FieldDescriptor, W extends FieldDescriptorTemplate<T>>
			implements FieldDescriptorTemplateWrapper<T, W> {
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
		public FieldDescriptorTemplate<T> rename(String name) {
			return new RenamedFieldDescriptorTemplate<>(name, source);
		}
	}

	/**
	 * 模板名称
	 */
	@Override
	String getName();

	@Override
	default FieldDescriptorTemplate<T> rename(String name) {
		return new RenamedFieldDescriptorTemplate<>(name, this);
	}
}
