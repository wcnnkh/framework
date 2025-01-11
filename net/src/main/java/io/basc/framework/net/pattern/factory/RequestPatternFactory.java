package io.basc.framework.net.pattern.factory;

import java.util.function.Predicate;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.util.collections.Elements;

public interface RequestPatternFactory extends Predicate<Function> {

	/**
	 * 测试是否支持
	 */
	@Override
	boolean test(Function function);

	Elements<RequestPattern> getRequestPatterns(Function function, Parameters parameters);
}
