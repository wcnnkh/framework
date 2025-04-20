package run.soeasy.framework.core.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.transform.mapping.PropertyDescriptors;

public interface MappingDescriptor<T extends FieldDescriptor> extends PropertyDescriptors<T>, Named {
	@FunctionalInterface
	public static interface MappingDescriptorWrapper<T extends FieldDescriptor, W extends MappingDescriptor<T>>
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
	public static class RenamedMappingDescriptor<T extends FieldDescriptor, W extends MappingDescriptor<T>>
			implements MappingDescriptorWrapper<T, W> {
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
			return new RenamedMappingDescriptor<>(name, source);
		}
	}

	/**
	 * 模板名称
	 */
	@Override
	String getName();

	@Override
	default MappingDescriptor<T> rename(String name) {
		return new RenamedMappingDescriptor<>(name, this);
	}
}
