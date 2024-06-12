package io.basc.framework.transform;

import java.util.LinkedHashMap;
import java.util.Map;

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

	default Map<String, Property> getMap(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		Map<String, Property> map = new LinkedHashMap<>();
		for (Property property : getElements()) {
			String name = property.getName();
			if (name.length() > prefix.length() && name.startsWith(prefix)) {
				name = name.substring(prefix.length());
				map.put(name, property);
			}
		}
		return map;
	}
}
