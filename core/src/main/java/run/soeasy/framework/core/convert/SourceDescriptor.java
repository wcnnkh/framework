package run.soeasy.framework.core.convert;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;

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
