package run.soeasy.framework.core.convert.value;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface Accessible extends Readable, Writeable {
	@FunctionalInterface
	public static interface AccessibleWrapper<W extends Accessible>
			extends Accessible, ReadableWrapper<W>, WriteableWrapper<W> {
		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}
	}

	@Data
	public static class SharedAccessDescriptor implements Accessible, Serializable {
		private static final long serialVersionUID = 1L;
		private TypeDescriptor typeDescriptor;
		private boolean present;
		private TypeDescriptor requriedTypeDescriptor;
		private boolean required;

		public SharedAccessDescriptor(@NonNull TypeDescriptor typeDescriptor) {
			this.typeDescriptor = typeDescriptor;
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
		return getTypeDescriptor();
	}
}
