package io.basc.framework.net.pattern;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;

public interface RequestPattern extends Predicate<Request>, Function<Request, Properties> {
	/**
	 * 获取匹配参数
	 */
	@Override
	Properties apply(Request request);

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
