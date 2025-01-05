package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.mapping.PropertyDescriptors;
import io.basc.framework.util.alias.Named;
import lombok.Data;
import lombok.NonNull;

public interface MappingDescriptor<T extends FieldDescriptor> extends PropertyDescriptors<T>, Named {
	@FunctionalInterface
	public static interface FieldDescriptorTemplateWrapper<T extends FieldDescriptor, W extends MappingDescriptor<T>>
			extends MappingDescriptor<T>, PropertyDescriptorsWrapper<T, W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default MappingDescriptor<T> rename(String name) {
			return getSource().rename(name);
		}
	}

	@Data
	public static class RenamedFieldDescriptorTemplate<T extends FieldDescriptor, W extends MappingDescriptor<T>>
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
		public MappingDescriptor<T> rename(String name) {
			return new RenamedFieldDescriptorTemplate<>(name, source);
		}
	}

	/**
	 * 模板名称
	 */
	@Override
	String getName();

	@Override
	default MappingDescriptor<T> rename(String name) {
		return new RenamedFieldDescriptorTemplate<>(name, this);
	}
}
