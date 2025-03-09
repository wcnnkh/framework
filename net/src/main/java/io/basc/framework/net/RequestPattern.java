package io.basc.framework.net;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.core.convert.transform.stereotype.Properties;

public interface RequestPattern extends Predicate<Request>, Function<Request, Properties> {
	public static final RequestPattern ANY_REQUEST_PATTERN = new WildcardRequestPattern();

	/**
	 * 获取匹配参数
	 */
	@Override
	Properties apply(Request request);

	@Override
	boolean equals(Object obj);

	MediaTypes getConsumes();

	MediaTypes getProduces();

	@Override
	int hashCode();

	@Override
	default boolean test(Request request) {
		MediaTypes consumes = getConsumes();
		if (consumes != null && !consumes.isCompatibleWith(request.getContentType())) {
			return false;
		}
		return true;
	}
}
