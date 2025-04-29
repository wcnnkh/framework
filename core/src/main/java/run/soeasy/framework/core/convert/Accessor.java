package run.soeasy.framework.core.convert;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

public interface Accessor extends Readable, Writeable {
	@FunctionalInterface
	public static interface AccessibleWrapper<W extends Accessor>
			extends Accessor, ReadableWrapper<W>, WriteableWrapper<W> {
		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}
	}

	@Data
	public static class SharedAccessDescriptor implements Accessor, Serializable {
		private static final long serialVersionUID = 1L;
		private TypeDescriptor returnTypeDescriptor;
		private TypeDescriptor requriedTypeDescriptor;
		private boolean required;

		public SharedAccessDescriptor(@NonNull TypeDescriptor typeDescriptor) {
			this.returnTypeDescriptor = typeDescriptor;
			this.requriedTypeDescriptor = typeDescriptor;
		}
	}

	/**
	 * 插入值时需要的类型, 默认情况下和{@link #getTypeDescriptor()}相同
	 * 
	 * @see #setValue(Object)
	 * @return
	 */
	default TypeDescriptor getRequiredTypeDescriptor() {
		return getReturnTypeDescriptor();
	}
}
