package io.basc.framework.net.pattern;

import io.basc.framework.execution.Function;
import io.basc.framework.util.element.Elements;

public interface RequestPatternFactory {
	Elements<RequestPattern> getRequestPatterns(Function function, Elements<? extends Object> args);
}
