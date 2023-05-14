package io.basc.framework.exec;

import io.basc.framework.util.Elements;

public abstract class AbstractExecutable implements Executable {

	@Override
	public final boolean isExecutable() {
		return isExecutable(Elements.empty());
	}

	@Override
	public final Object execute() {
		return execute(Elements.empty());
	}
}
