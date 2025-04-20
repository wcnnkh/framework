package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;

/**
 * 多个参数的定义
 * 
 * @author shuchaowen
 *
 */
public interface ParameterSource extends Dictionary<Parameter>, ParameterDescriptors<Parameter> {
	public static interface ParameterSourceWrapper<W extends ParameterSource>
			extends ParameterSource, DictionaryWrapper<Parameter, W>, ParameterDescriptorsWrapper<Parameter, W> {

		@Override
		default Elements<KeyValue<Object, Parameter>> getElements() {
			return getSource().getElements();
		}

		@Override
		default ParameterSource rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedParameterSource<W extends ParameterSource> extends RenamedTemplate<Object, Parameter, W>
			implements ParameterSourceWrapper<W> {

		public RenamedParameterSource(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public ParameterSource rename(String name) {
			return new RenamedParameterSource<>(getSource(), name);
		}
	}

	default Object[] getArgs() {
		return map((e) -> e.get()).toArray(Object[]::new);
	}

	@Override
	default Elements<KeyValue<Object, Parameter>> getElements() {
		return ParameterDescriptors.super.getElements();
	}

	default Class<?>[] getTypes() {
		return map((e) -> e.getTypeDescriptor().getType()).toArray(Class<?>[]::new);
	}

	@Override
	default ParameterSource rename(String name) {
		return new RenamedParameterSource<>(this, name);
	}
}
