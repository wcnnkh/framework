package io.basc.framework.net.pattern.factory;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.util.collections.Elements;

public class ConfigurableRequestPatternFactory extends ConfigurableServices<RequestPatternFactory>
		implements RequestPatternFactory {
	public ConfigurableRequestPatternFactory() {
		setServiceClass(RequestPatternFactory.class);
	}

	@Override
	public boolean test(Function function) {
		return getServices().anyMatch((e) -> e.test(function));
	}

	@Override
	public Elements<RequestPattern> getRequestPatterns(Function function, Parameters parameters) {
		Elements<RequestPattern> elements = Elements.empty();
		for (RequestPatternFactory factory : getServices()) {
			if (factory.test(function)) {
				elements = elements.concat(factory.getRequestPatterns(function, parameters));
			}
		}
		return elements;
	}

}
