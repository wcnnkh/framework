package io.basc.framework.transform;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Items;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;

public interface Properties extends Items<Property> {
	default Elements<Property> getElements(String pattern, StringMatcher keyMatcher) {
		Assert.requiredArgument(pattern != null, "pattern");
		Assert.requiredArgument(keyMatcher != null, "keyMatcher");
		return getElements().filter((property) -> StringMatchers.match(keyMatcher, pattern, property.getName())
				|| property.getAliasNames().anyMatch((name) -> StringMatchers.match(keyMatcher, pattern, name)));
	}

	default Parameters toParameters() {
		return () -> getElements().map((e) -> e);
	}
}
