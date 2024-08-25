package io.basc.framework.net.pattern;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
import io.basc.framework.observe.service.ObservableServiceLoader;

/**
 * 多个RequestPattern
 * 
 * @author shuchaowen
 *
 */
public class RequestPatterns extends ObservableServiceLoader<RequestPattern> implements RequestPattern {

	@Override
	public boolean test(Request request) {
		return getServices().anyMatch((e) -> e.test(request));
	}

	@Override
	public Parameters apply(Request request) {
		for (RequestPattern pattern : getServices()) {
			if (pattern.test(request)) {
				return pattern.apply(request);
			}
		}
		return Parameters.empty();
	}

	@Override
	public MimeTypes getConsumes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : getServices()) {
			mimeTypes.addAll(pattern.getConsumes());
		}
		return mimeTypes;
	}

	@Override
	public MimeTypes getProduces() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : getServices()) {
			mimeTypes.addAll(pattern.getProduces());
		}
		return mimeTypes;
	}
}
