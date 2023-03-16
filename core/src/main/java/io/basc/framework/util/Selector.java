package io.basc.framework.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

public interface Selector<E> {
	@Nullable
	default E select(Stream<E> elements) {
		if (elements == null) {
			return null;
		}

		return select(elements.collect(Collectors.toList()));
	}

	@Nullable
	E select(List<E> elements);
}
