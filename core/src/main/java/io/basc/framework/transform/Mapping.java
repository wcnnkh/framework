package io.basc.framework.transform;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Listable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;

public interface Mapping<P extends Property> extends Listable<P> {
	default Elements<P> getElements(String name) {
		return getElements().filter((e) -> StringUtils.equals(name, e.getName()));
	}

	default Elements<P> getElements(String pattern, StringMatcher matcher) {
		Assert.requiredArgument(pattern != null, "pattern");
		Assert.requiredArgument(matcher != null, "matcher");
		return getElements().filter((property) -> StringMatchers.match(matcher, pattern, property.getName()));
	}
}
