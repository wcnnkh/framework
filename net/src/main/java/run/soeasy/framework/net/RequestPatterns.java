package run.soeasy.framework.net;

import java.util.HashSet;
import java.util.Set;

import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.register.container.CollectionContainer;
import run.soeasy.framework.util.register.container.ElementRegistration;

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
}
