package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public interface ObjectAccess<E extends Throwable> {
	TypeDescriptor getTypeDescriptor();
	
	Elements<String> keys() throws E;

	Parameter get(String name) throws E;

	void set(Parameter parameter) throws E;

	default void set(Iterable<? extends Parameter> parameters) throws E {
		if (parameters == null) {
			return;
		}

		set(parameters.iterator());
	}

	default void set(Iterator<? extends Parameter> parameters) throws E {
		if (parameters == null) {
			return;
		}

		while (parameters.hasNext()) {
			set(parameters.next());
		}
	}

	default void copy(ObjectAccess<? extends E> targetAccess) throws E {
		copy(targetAccess, Function.identity());
	}

	default void copyByPrefix(ObjectAccess<? extends E> targetAccess, String prefix) throws E {
		copy(targetAccess, StringUtils.isEmpty(prefix) ? null : (key) -> {
			if (prefix == null || key.startsWith(prefix)) {
				return key.substring(prefix.length());
			}
			return null;
		});
	}

	default void copy(ObjectAccess<? extends E> targetAccess, @Nullable Function<String, String> keyFunction) throws E {
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