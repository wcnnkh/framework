package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface AccessibleDescriptor extends SourceDescriptor, TargetDescriptor {
	@FunctionalInterface
	public static interface AccessibleDescriptorWrapper<W extends AccessibleDescriptor>
			extends AccessibleDescriptor, SourceDescriptorWrapper<W>, TargetDescriptorWrapper<W> {
		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default boolean isWriteable() {
			return getSource().isWriteable();
		}
	}

	public static AccessibleDescriptor forTypeDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		return new CustomizeAccessibleDescriptor(typeDescriptor);
	}

	/**
	 * 是否可读
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return true;
	}

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	default boolean isWriteable() {
		return true;
	}
}
