package io.basc.framework.util.alias;

import io.basc.framework.util.Wrapper;

public interface NamedWrapper<W extends Named> extends Named, Wrapper<W> {

	@Override
	default String getName() {
		return getSource().getName();
	}
}
