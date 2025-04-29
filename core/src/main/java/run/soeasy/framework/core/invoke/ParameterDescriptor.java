package run.soeasy.framework.core.invoke;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.mapping.PropertyDescriptor;

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

	public static class RenamedParameterDescriptor<W extends ParameterDescriptor> extends RenamedPropertyDescriptor<W>
			implements ParameterDescriptorWrapper<W> {

		public RenamedParameterDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public ParameterDescriptor rename(String name) {
			return new RenamedParameterDescriptor<>(name, getSource());
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class ReindexParameterDescriptor<W extends ParameterDescriptor>
			implements ParameterDescriptorWrapper<W> {
		private final int index;
		@NonNull
		private final W source;

		@Override
		public ParameterDescriptor reindex(int index) {
			return new ReindexParameterDescriptor<>(index, source);
		}
	}

	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	int getIndex();

	default ParameterDescriptor rename(String name) {
		return new RenamedParameterDescriptor<>(name, this);
	}

	default ParameterDescriptor reindex(int index) {
		return new ReindexParameterDescriptor<>(index, this);
	}
}
