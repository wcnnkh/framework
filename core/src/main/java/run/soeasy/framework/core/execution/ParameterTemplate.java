package run.soeasy.framework.core.execution;

import java.util.function.Function;
import java.util.stream.IntStream;

import lombok.NonNull;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyTemplate;
import run.soeasy.framework.util.collections.Elements;

public interface ParameterTemplate<T extends Parameter> extends PropertyTemplate<T>, ParameterDescriptorTemplate {
	public static interface ParameterMappingWrapper<T extends Parameter, W extends ParameterTemplate<T>>
			extends ParameterTemplate<T>, PropertyMappingWrapper<T, W>, ParameterDescriptorTemplateWrapper<W> {

		@Override
		default T get(int index) {
			return getSource().get(index);
		}

		@Override
		default Elements<T> getAccessors(@NonNull Object key) {
			return getSource().getAccessors(key);
		}

		@Override
		default Object[] getArgs() {
			return getSource().getArgs();
		}

		@Override
		default Elements<T> getElements() {
			return getSource().getElements();
		}

		@Override
		default Class<?>[] getTypes() {
			return getSource().getTypes();
		}

		@Override
		default int getValidCount() {
			return getSource().getValidCount();
		}

		@Override
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}

		@Override
		default Elements<T> getValidElements() {
			return getSource().getValidElements();
		}

		@Override
		default boolean isValidated() {
			return getSource().isValidated();
		}

		@Override
		default int size() {
			return getSource().size();
		}
	}

	T get(int index);

	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return getElements().map(Function.identity());
	}

	@Override
	default Elements<T> getAccessors(@NonNull Object key) {
		if (key instanceof Number) {
			int index = ((Number) key).intValue();
			T parameter = get(index);
			if (parameter == null) {
				return Elements.empty();
			}

			return Elements.singleton(parameter);
		}
		return PropertyTemplate.super.getAccessors(key);
	}

	default Object[] getArgs() {
		Object[] args = new Object[size()];
		for (int i = 0; i < args.length; i++) {
			Parameter parameter = get(i);
			if (parameter == null || !parameter.isReadable()) {
				continue;
			}
			args[i] = parameter.get();
		}
		return args;
	}

	@Override
	default Elements<T> getElements() {
		return Elements.of(() -> IntStream.range(0, size() - 1).mapToObj((i) -> get(i)));
	}

	default Class<?>[] getTypes() {
		Class<?>[] types = new Class<?>[size()];
		for (int i = 0; i < types.length; i++) {
			Parameter parameter = get(i);
			if (parameter == null) {
				types[i] = Object.class;
			} else {
				types[i] = parameter.getTypeDescriptor().getType();
			}
		}
		return types;
	}

	default int getValidCount() {
		int len = 0;
		for (Parameter parameter : getElements()) {
			if (parameter.getIndex() >= 0) {
				len++;
			}
		}
		return len;
	}

	default Elements<T> getValidElements() {
		return getElements().filter((e) -> e != null && e.isReadable());
	}

	/**
	 * 所有参数是否合法
	 * 
	 * @return
	 */
	default boolean isValidated() {
		for (Parameter parameter : getElements()) {
			if (parameter.getIndex() < 0) {
				return false;
			}
		}
		return true;
	}

	int size();
}
