package io.basc.framework.value;

import java.util.stream.Stream;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;

public interface PropertyFactory extends ValueFactory<String> {

	default boolean containsKey(String key) {
		Value value = get(key);
		return value != null && value.isPresent();
	}

	Elements<String> keys();

	default Stream<String> stream(String pattern, StringMatcher keyMatcher) {
		Assert.requiredArgument(pattern != null, "pattern");
		Assert.requiredArgument(keyMatcher != null, "keyMatcher");
		return keys().stream().filter((t) -> StringMatchers.match(keyMatcher, pattern, t));
	}

	default Stream<Pair<String, Value>> streamByPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		return keys().stream().filter((k) -> k.length() > prefix.length() && k.startsWith(prefix))
				.map((k) -> new Pair<>(k.substring(prefix.length()), get(k)));
	}
}
