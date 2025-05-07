package run.soeasy.framework.core.invoke;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.property.PropertyTemplate;

@FunctionalInterface
public interface ParameterTemplate<T extends ParameterDescriptor> extends PropertyTemplate<T> {
	public static class EmptyParameterTemplate<T extends ParameterDescriptor> extends EmptyPropertyTemplate<T>
			implements ParameterTemplate<T> {
		private static final long serialVersionUID = 1L;
		private static final ParameterTemplate<?> EMPTY_PARAMETER_TEMPLATE = new EmptyParameterTemplate<>();
	}

	@SuppressWarnings("unchecked")
	public static <T extends ParameterDescriptor> ParameterTemplate<T> empty() {
		return (ParameterTemplate<T>) EmptyParameterTemplate.EMPTY_PARAMETER_TEMPLATE;
	}

	@FunctionalInterface
	public static interface ParameterDescriptorsWrapper<T extends ParameterDescriptor, W extends ParameterTemplate<T>>
			extends PropertyTemplate<T>, PropertyTemplateWrapper<T, W> {
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
