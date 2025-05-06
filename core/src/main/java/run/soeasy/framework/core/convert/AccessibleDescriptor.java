package run.soeasy.framework.core.convert;

import lombok.Data;
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

	@Data
	public static class DefaultAccessibleDescriptor implements AccessibleDescriptor {
		@NonNull
		private TypeDescriptor returnTypeDescriptor;
		@NonNull
		private TypeDescriptor requiredTypeDescriptor;
		private boolean requried = false;
		private boolean readable = true;
		private boolean writeable = true;

		public DefaultAccessibleDescriptor(@NonNull TypeDescriptor typeDescriptor) {
			this.requiredTypeDescriptor = typeDescriptor;
			this.returnTypeDescriptor = typeDescriptor;
		}
	}

	public static AccessibleDescriptor forTypeDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		return new DefaultAccessibleDescriptor(typeDescriptor);
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
