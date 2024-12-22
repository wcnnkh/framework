package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface AccessDescriptor extends ValueDescriptor {
	@FunctionalInterface
	public static interface AccessDescriptorWrapper<W extends AccessDescriptor>
			extends AccessDescriptor, ValueDescriptorWrapper<W> {
		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}

		@Override
		default boolean isRequired() {
			return getSource().isRequired();
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

	/**
	 * 是否是必需的
	 * 
	 * @return true表示set是不能插入空值
	 */
	default boolean isRequired() {
		return false;
	}
}
