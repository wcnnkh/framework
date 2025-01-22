package io.basc.framework.net.pattern;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
import io.basc.framework.util.collections.Elements;
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
	public Properties apply(Request request) {
		for (RequestPattern pattern : this) {
			if (pattern.test(request)) {
				return pattern.apply(request);
			}
		}
		return Parameters.EMPTY_PARAMETERS;
	}

	@Override
	public MimeTypes getConsumes() {
		Elements<MimeType> elements = flatMap((e) -> e.getConsumes());
		return MimeTypes.forElements(elements);
	}

	@Override
	public MimeTypes getProduces() {
		Elements<MimeType> elements = flatMap((e) -> e.getProduces());
		return MimeTypes.forElements(elements);
	}
}
