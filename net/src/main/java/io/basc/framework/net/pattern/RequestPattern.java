package io.basc.framework.net.pattern;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;

public interface RequestPattern extends Predicate<Request>, Function<Request, Parameters> {
	/**
	 * 获取匹配参数
	 */
	@Override
	default Parameters apply(Request request) {
		return Parameters.empty();
	}

	@Override
	boolean equals(Object obj);

	MimeTypes getConsumes();

	MimeTypes getProduces();

	@Override
	int hashCode();

	@Override
	default boolean test(Request request) {
		MimeTypes consumes = getConsumes();
		if (consumes != null && !consumes.isCompatibleWith(request.getContentType())) {
			return false;
		}
		return true;
	}
}
