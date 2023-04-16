package io.basc.framework.value;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;

public interface PropertyFactory extends ValueFactory<String> {

	Elements<String> keys();

	default boolean containsKey(String key) {
		Value value = get(key);
		return value != null && value.isPresent();
	}

	default Elements<String> keys(String pattern, StringMatcher keyMatcher) {
		Assert.requiredArgument(pattern != null, "pattern");
		Assert.requiredArgument(keyMatcher != null, "keyMatcher");
		return keys().filter((t) -> StringMatchers.match(keyMatcher, pattern, t));
	}

	default Elements<Pair<String, Value>> streamByPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		return keys().filter((k) -> k.length() > prefix.length() && k.startsWith(prefix))
				.map((k) -> new Pair<>(k.substring(prefix.length()), get(k)));
	}
}
