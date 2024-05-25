package io.basc.framework.mapper.property;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;

public class NamedWrapper<W extends Named> extends Wrapper<W> implements Named {

	public NamedWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public Elements<String> getAliasNames() {
		return wrappedTarget.getAliasNames();
	}
}
