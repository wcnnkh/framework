package run.soeasy.framework.core.invoke;

import java.io.Serializable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.property.PropertyDescriptor;

public interface ParameterDescriptor extends PropertyDescriptor {
	@FunctionalInterface
	public static interface ParameterDescriptorWrapper<W extends ParameterDescriptor>
			extends ParameterDescriptor, PropertyDescriptorWrapper<W> {
		@Override
		default int getIndex() {
			return getSource().getIndex();
		}

		@Override
		default ParameterDescriptor rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedParameterDescriptor<W extends ParameterDescriptor> extends NamedPropertyDescriptor<W>
			implements ParameterDescriptorWrapper<W> {

		private static final long serialVersionUID = 1L;

		public RenamedParameterDescriptor(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public ParameterDescriptor rename(String name) {
			return new RenamedParameterDescriptor<>(getSource(), name);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class ReindexParameterDescriptor<W extends ParameterDescriptor>
			implements ParameterDescriptorWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private final W source;
		private final int index;

		@Override
		public ParameterDescriptor reindex(int index) {
			return new ReindexParameterDescriptor<>(source, index);
		}
	}

	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	int getIndex();

	default ParameterDescriptor rename(String name) {
		return new RenamedParameterDescriptor<>(this, name);
	}

	default ParameterDescriptor reindex(int index) {
		return new ReindexParameterDescriptor<>(this, index);
	}
}
