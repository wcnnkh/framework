package io.basc.framework.net.pattern;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableRequestPatternFactory extends ConfigurableServices<RequestPatternFactory>
		implements RequestPatternFactory {
	public ConfigurableRequestPatternFactory() {
		setServiceClass(RequestPatternFactory.class);
	}

	@Override
	public boolean test(Function function) {
		return anyMatch((e) -> e.test(function));
	}

	@Override
	public Elements<RequestPattern> getRequestPatterns(Function function, Parameters parameters) {
		Elements<RequestPattern> elements = Elements.empty();
		for (RequestPatternFactory factory : this) {
			if (factory.test(function)) {
				elements = elements.concat(factory.getRequestPatterns(function, parameters));
			}
		}
		return elements;
	}

}
