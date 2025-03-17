package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface AccessDescriptor extends SourceDescriptor, TargetDescriptor {
	@FunctionalInterface
	public static interface AccessDescriptorWrapper<W extends AccessDescriptor>
			extends AccessDescriptor, SourceDescriptorWrapper<W>, TargetDescriptorWrapper<W> {
		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class SharedAccessDescriptor extends SharedValueDescriptor implements AccessDescriptor {
		private static final long serialVersionUID = 1L;
		@NonNull
		private TypeDescriptor requriedTypeDescriptor;
		private boolean required;

		public SharedAccessDescriptor(@NonNull TypeDescriptor typeDescriptor) {
			super(typeDescriptor);
			this.requriedTypeDescriptor = typeDescriptor;
		}
	}

	public static SharedAccessDescriptor of(@NonNull TypeDescriptor typeDescriptor) {
		return new SharedAccessDescriptor(typeDescriptor);
	}

	/**
	 * 插入值时需要的类型, 默认情况下和{@link #getTypeDescriptor()}相同
	 * 
	 * @see #setValue(Object)
	 * @return
	 */
	default TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}
}
