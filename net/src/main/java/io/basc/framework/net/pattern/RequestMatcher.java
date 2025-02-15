package io.basc.framework.net.pattern;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.env.PropertySource;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;

/**
 * request匹配器
 * 
 * @author shuchaowen
 *
 */
@Getter
public class RequestMatcher implements RequestPattern {
	private final RequestPatterns include = new RequestPatterns();
	private final RequestPatterns exclude = new RequestPatterns();

	@Override
	public boolean test(Request request) {
		return !exclude.test(request) && include.test(request);
	}

	public RequestMatcher include(RequestPattern requestPattern) {
		include.register(requestPattern);
		return this;
	}

	public RequestMatcher exclude(RequestPattern requestPattern) {
		exclude.register(requestPattern);
		return this;
	}

	@Override
	public Properties apply(Request request) {
		if (exclude.test(request)) {
			return PropertySource.EMPTY_PROPERTIES;
		}

		return include.apply(request);
	}

	@Override
	public MimeTypes getConsumes() {
		Elements<MimeType> elements = include.getConsumes().filter((e) -> !exclude.contains(e));
		return MimeTypes.forElements(elements);
	}

	@Override
	public MimeTypes getProduces() {
		Elements<MimeType> elements = include.getProduces().filter((e) -> !exclude.contains(e));
		return MimeTypes.forElements(elements);
	}

}
