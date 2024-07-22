package io.basc.framework.net.pattern;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
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
	public Parameters apply(Request request) {
		if (exclude.test(request)) {
			return Parameters.empty();
		}

		return include.apply(request);
	}

	@Override
	public MimeTypes getConsumes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : include.getServices()) {
			if (exclude.getServices().contains(pattern)) {
				continue;
			}
			mimeTypes.addAll(pattern.getConsumes());
		}
		return mimeTypes;
	}

	@Override
	public MimeTypes getProduces() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : include.getServices()) {
			if (exclude.getServices().contains(pattern)) {
				continue;
			}
			mimeTypes.addAll(pattern.getProduces());
		}
		return mimeTypes;
	}

}
