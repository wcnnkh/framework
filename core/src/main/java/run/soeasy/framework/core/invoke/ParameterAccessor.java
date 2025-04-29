package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;

public interface ParameterAccessor extends ParameterDescriptor, PropertyAccessor {

	@FunctionalInterface
	public static interface ParameterAccessorWrapper<W extends ParameterAccessor>
			extends ParameterAccessor, PropertyAccessorWrapper<W>, ParameterDescriptorWrapper<W> {
		@Override
		default ParameterAccessor rename(String name) {
			return ParameterAccessor.super.rename(name);
		}
	}

	public static class RenamedParameterAccessor<W extends ParameterAccessor> extends RenamedParameterDescriptor<W>
			implements ParameterAccessorWrapper<W> {

		public RenamedParameterAccessor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public ParameterAccessor rename(String name) {
			return new RenamedParameterAccessor<>(name, getSource());
		}
	}

	public static class ReindexParameterAccessor<W extends ParameterAccessor> extends ReindexParameterDescriptor<W>
			implements ParameterAccessorWrapper<W> {

		public ReindexParameterAccessor(int index, @NonNull W source) {
			super(index, source);
		}

		@Override
		public ParameterAccessor reindex(int index) {
			return new ReindexParameterAccessor<>(index, getSource());
		}
	}

	@Override
	default ParameterAccessor rename(String name) {
		return new RenamedParameterAccessor<>(name, this);
	}

	@Override
	default ParameterDescriptor reindex(int index) {
		return new ReindexParameterAccessor<>(index, this);
	}
}
