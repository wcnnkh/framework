package io.basc.framework.execution.aop;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Constructor;
import io.basc.framework.util.element.Elements;

public interface Proxy extends Constructor {

	@Override
	Object execute();

	@Override
	Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args);
}
