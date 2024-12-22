package io.basc.framework.core.convert;

import java.io.Serializable;

import io.basc.framework.util.Wrapper;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface ValueDescriptor {
	@FunctionalInterface
	public static interface ValueDescriptorWrapper<W extends ValueDescriptor> extends ValueDescriptor, Wrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}
	}

	@Data
	public static class SharedValueDescriptor implements ValueDescriptor, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private TypeDescriptor typeDescriptor;
	}

	TypeDescriptor getTypeDescriptor();
}
