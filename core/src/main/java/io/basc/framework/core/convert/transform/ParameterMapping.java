package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import lombok.NonNull;

public interface ParameterMapping<T extends Parameter> extends PropertyMapping<T> {
	public static interface ParameterMappingWrapper<T extends Parameter, W extends ParameterMapping<T>>
			extends ParameterMapping<T>, PropertyMappingWrapper<T, W> {

		@Override
		default T get(int index) {
			return getSource().get(index);
		}

		@Override
		default Elements<T> getAccesses(@NonNull Object key) {
			return getSource().getAccesses(key);
		}

		@Override
		default Object[] getArgs() {
			return getSource().getArgs();
		}

		@Override
		default Class<?>[] getTypes() {
			return getSource().getTypes();
		}

		@Override
		default int size() {
			return getSource().size();
		}
	}

	T get(int index);

	@Override
	default Elements<T> getAccesses(@NonNull Object key) {
		if (key instanceof Number) {
			int index = ((Number) key).intValue();
			T parameter = get(index);
			if (parameter == null) {
				return Elements.empty();
			}

			return Elements.singleton(parameter);
		}
		return PropertyMapping.super.getAccesses(key);
	}

	default Object[] getArgs() {
		Object[] args = new Object[size()];
		for (int i = 0; i < args.length; i++) {
			T parameter = get(i);
			args[i] = parameter.get();
		}
		return args;
	}

	default Class<?>[] getTypes() {
		Class<?>[] types = new Class<?>[size()];
		for (int i = 0; i < types.length; i++) {
			T parameter = get(i);
			types[i] = parameter.getTypeDescriptor().getType();
		}
		return types;
	}

	int size();
}
