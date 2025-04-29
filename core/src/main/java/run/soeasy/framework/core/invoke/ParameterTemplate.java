package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.mapping.Dictionary;

/**
 * 多个参数的定义
 * 
 * @author shuchaowen
 *
 */
public interface ParameterTemplate extends Dictionary<ParameterAccessor>, ParameterDescriptors<ParameterAccessor> {
	public static interface ParameterSourceWrapper<W extends ParameterTemplate> extends ParameterTemplate,
			DictionaryWrapper<ParameterAccessor, W>, ParameterDescriptorsWrapper<ParameterAccessor, W> {

		@Override
		default Elements<KeyValue<Object, ParameterAccessor>> getElements() {
			return getSource().getElements();
		}

		@Override
		default ParameterTemplate rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedParameterSource<W extends ParameterTemplate>
			extends RenamedTemplate<Object, ParameterAccessor, W> implements ParameterSourceWrapper<W> {

		public RenamedParameterSource(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public ParameterTemplate rename(String name) {
			return new RenamedParameterSource<>(getSource(), name);
		}
	}

	default Object[] getArgs() {
		return map((e) -> e.get()).toArray(Object[]::new);
	}

	@Override
	default Elements<KeyValue<Object, ParameterAccessor>> getElements() {
		return ParameterDescriptors.super.getElements();
	}

	default Class<?>[] getTypes() {
		return map((e) -> e.getReturnTypeDescriptor().getType()).toArray(Class<?>[]::new);
	}

	default boolean isValidated() {
		return getElements().allMatch((e) -> {
			if (!e.getValue().isReadable()) {
				return false;
			}

			if (e.getValue().isRequired() && e.getValue().get() == null) {
				return false;
			}
			return true;
		});
	}

	@Override
	default ParameterTemplate rename(String name) {
		return new RenamedParameterSource<>(this, name);
	}
}
