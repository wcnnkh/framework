package io.basc.framework.util;

import java.util.stream.Collectors;

import io.basc.framework.lang.Nullable;

public interface Name {

	/**
	 * 分割符
	 * 
	 * @return
	 */
	@Nullable
	CharSequence getDelimiter();

	Elements<? extends Name> getElements();

	default String getName() {
		Elements<String> elements = getElements().map((e) -> e.getName());
		CharSequence delimiter = getDelimiter();
		return delimiter == null ? elements.collect(Collectors.joining())
				: elements.collect(Collectors.joining(delimiter));
	}
}
