package io.basc.framework.net.pattern;

import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
import io.basc.framework.util.spi.Services;

/**
 * 多个RequestPattern
 * 
 * @author shuchaowen
 *
 */
public class RequestPatterns extends Services<RequestPattern> implements RequestPattern {

	@Override
	public boolean test(Request request) {
		return anyMatch((e) -> e.test(request));
	}

	@Override
	public Parameters apply(Request request) {
		for (RequestPattern pattern : this) {
			if (pattern.test(request)) {
				return pattern.apply(request);
			}
		}
		return Parameters.EMPTY_PARAMETERS;
	}

	@Override
	public MimeTypes getConsumes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : this) {
			mimeTypes.addAll(pattern.getConsumes());
		}
		return mimeTypes;
	}

	@Override
	public MimeTypes getProduces() {
		MimeTypes mimeTypes = new MimeTypes();
		for (RequestPattern pattern : this) {
			mimeTypes.addAll(pattern.getProduces());
		}
		return mimeTypes;
	}
}
