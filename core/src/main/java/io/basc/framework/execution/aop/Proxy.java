package io.basc.framework.execution.aop;

import io.basc.framework.execution.Constructable;
import io.basc.framework.util.element.Elements;

public interface Proxy extends Constructable {
	@Override
	default Object execute() {
		return execute(Elements.empty(), Elements.empty());
	}

	@Override
	Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args);
}
