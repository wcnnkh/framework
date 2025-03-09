package io.basc.framework.net;

import java.util.HashSet;
import java.util.Set;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.uri.PathPattern;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.container.CollectionContainer;
import io.basc.framework.util.register.container.ElementRegistration;

/**
 * 多个RequestPattern
 * 
 * @author shuchaowen
 *
 */
public class RequestPatterns extends CollectionContainer<RequestPattern, Set<ElementRegistration<RequestPattern>>>
		implements RequestPattern {
	public RequestPatterns() {
		super(HashSet::new);
	}

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
		return Properties.EMPTY_PROPERTIES;
	}

	@Override
	public MediaTypes getConsumes() {
		Elements<MediaType> elements = flatMap((e) -> e.getConsumes());
		return MediaTypes.forElements(elements);
	}

	@Override
	public MediaTypes getProduces() {
		Elements<MediaType> elements = flatMap((e) -> e.getProduces());
		return MediaTypes.forElements(elements);
	}

	public Registration register(String path) {
		PathPattern pathPattern = new PathPattern();
		pathPattern.setPath(path);
		return register(pathPattern);
	}
}
