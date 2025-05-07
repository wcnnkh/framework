package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.convert.property.PropertyAccessor;

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

		private static final long serialVersionUID = 1L;

		public RenamedParameterAccessor(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public ParameterAccessor rename(String name) {
			return new RenamedParameterAccessor<>(getSource(), name);
		}
	}

	public static class ReindexParameterAccessor<W extends ParameterAccessor> extends ReindexParameterDescriptor<W>
			implements ParameterAccessorWrapper<W> {

		private static final long serialVersionUID = 1L;

		public ReindexParameterAccessor(@NonNull W source, int index) {
			super(source, index);
		}

		@Override
		public ParameterAccessor reindex(int index) {
			return new ReindexParameterAccessor<>(getSource(), index);
		}
	}

	@Override
	default ParameterAccessor rename(String name) {
		return new RenamedParameterAccessor<>(this, name);
	}

	@Override
	default ParameterDescriptor reindex(int index) {
		return new ReindexParameterAccessor<>(this, index);
	}
}
