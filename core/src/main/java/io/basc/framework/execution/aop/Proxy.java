package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executable;
import io.basc.framework.util.element.Elements;

public interface Proxy extends Executable {
	Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args);
}
