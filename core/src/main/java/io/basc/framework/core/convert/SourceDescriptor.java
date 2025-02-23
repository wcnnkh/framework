package io.basc.framework.core.convert;

import java.io.Serializable;

import io.basc.framework.util.function.Wrapper;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface SourceDescriptor {
	@FunctionalInterface
	public static interface SourceDescriptorWrapper<W extends SourceDescriptor> extends SourceDescriptor, Wrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}
	}

	@Data
	public static class SharedValueDescriptor implements SourceDescriptor, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private TypeDescriptor typeDescriptor;
	}

	TypeDescriptor getTypeDescriptor();
}
