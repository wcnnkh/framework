package io.basc.framework.net.pattern.factory;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.util.collection.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultRequestPatternFactory extends ConfigurableRequestPatternFactory {
	@NonNull
	private RequestPatternFactory groundRequestPatternFactory = GlobalRequestPatternFactory.getInstance();

	@Override
	public boolean test(Function function) {
		return super.test(function) || groundRequestPatternFactory.test(function);
	}

	@Override
	public Elements<RequestPattern> getRequestPatterns(Function function, Parameters parameters) {
		Elements<RequestPattern> elements = super.getRequestPatterns(function, parameters);
		if (groundRequestPatternFactory.test(function)) {
			elements = elements.concat(groundRequestPatternFactory.getRequestPatterns(function, parameters));
		}
		return elements;
	}
}
