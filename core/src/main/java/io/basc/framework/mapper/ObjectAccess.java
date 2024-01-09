package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public interface ObjectAccess {
	TypeDescriptor getTypeDescriptor();

	Elements<String> keys() throws MappingException;

	@Nullable
	Parameter get(String name) throws MappingException;

	void set(Parameter parameter) throws MappingException;

	default void set(Iterable<? extends Parameter> parameters) throws MappingException {
		if (parameters == null) {
			return;
		}

		set(parameters.iterator());
	}

	default void set(Iterator<? extends Parameter> parameters) throws MappingException {
		if (parameters == null) {
			return;
		}

		while (parameters.hasNext()) {
			set(parameters.next());
		}
	}

	default void copy(ObjectAccess targetAccess) throws MappingException {
		copy(targetAccess, Function.identity());
	}

	default void copyByPrefix(ObjectAccess targetAccess, String prefix) throws MappingException {
		copy(targetAccess, StringUtils.isEmpty(prefix) ? null : (key) -> {
			if (prefix == null || key.startsWith(prefix)) {
				return key.substring(prefix.length());
			}
			return null;
		});
	}

	default void copy(ObjectAccess targetAccess, @Nullable Function<String, String> keyFunction)
			throws MappingException {
		if (targetAccess == null) {
			return;
		}

		for (String key : keys()) {
			if (key == null) {
				continue;
			}

			String useKey = keyFunction == null ? key : keyFunction.apply(key);
			if (useKey == null) {
				continue;
			}

			Parameter parameter = get(key);
			if (!StringUtils.equals(key, useKey)) {
				parameter = parameter.rename(useKey);
			}
			targetAccess.set(parameter);
		}
	}
}