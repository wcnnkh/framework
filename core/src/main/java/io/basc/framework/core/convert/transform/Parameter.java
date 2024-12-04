package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface Parameter extends ParameterDescriptor, Property {
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class SimpleParmeter extends SimpleProperty implements Parameter {
		private static final long serialVersionUID = 1L;
		private int index;
		private boolean required;

		public SimpleParmeter(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name, typeDescriptor);
			this.index = index;
		}
	}

	public static Parameter of(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
		return new SimpleParmeter(index, name, typeDescriptor);
	}
}
