package run.soeasy.framework.net;

import lombok.Getter;
import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.core.env.PropertySource;
import run.soeasy.framework.util.collections.Elements;

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
	public MediaTypes getConsumes() {
		Elements<MediaType> elements = include.filter((e) -> !exclude.contains(e)).flatMap((e) -> e.getConsumes());
		return MediaTypes.forElements(elements);
	}

	@Override
	public MediaTypes getProduces() {
		Elements<MediaType> elements = include.filter((e) -> !exclude.contains(e)).flatMap((e) -> e.getProduces());
		return MediaTypes.forElements(elements);
	}

}
