package io.basc.framework.util;

public interface NamedWrapper<W extends Named> extends Named, Wrapper<W> {

	@Override
	default String getName() {
		return getSource().getName();
	}

	@Override
	default Elements<String> getAliasNames() {
		return getSource().getAliasNames();
	}
}
