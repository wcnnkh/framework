package run.soeasy.framework.core.convert;

import lombok.NonNull;

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
		return new SimpleAccessibleDescriptor(typeDescriptor);
	}

	/**
	 * 是否可读
	 * 
	 * @return
	 */
	boolean isReadable();

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	boolean isWriteable();
}
