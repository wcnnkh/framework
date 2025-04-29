package run.soeasy.framework.core.transform.mapping;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.mapping.PropertyDescriptors;

@FunctionalInterface
public interface ParameterDescriptors<T extends ParameterDescriptor> extends PropertyDescriptors<T> {
	public static class EmptyParameterDescriptors<T extends ParameterDescriptor> extends EmptyPropertyDescriptors<T>
			implements ParameterDescriptors<T> {
		private static final long serialVersionUID = 1L;
		private static final ParameterDescriptors<?> EMPTY_PARAMETER_DESCRIPTORS = new EmptyParameterDescriptors<>();
	}

	@SuppressWarnings("unchecked")
	public static <T extends ParameterDescriptor> ParameterDescriptors<T> empty() {
		return (ParameterDescriptors<T>) EmptyParameterDescriptors.EMPTY_PARAMETER_DESCRIPTORS;
	}

	@FunctionalInterface
	public static interface ParameterDescriptorsWrapper<T extends ParameterDescriptor, W extends ParameterDescriptors<T>>
			extends PropertyDescriptors<T>, PropertyDescriptorsWrapper<T, W> {
		@Override
		default Elements<KeyValue<Object, T>> getElements() {
			return getSource().getElements();
		}
	}

	@Override
	default Elements<KeyValue<Object, T>> getElements() {
		return indexed().map((e) -> KeyValue.of((int) e.getIndex(), e.getElement()));
	}
}
