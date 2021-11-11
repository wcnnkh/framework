package io.basc.framework.value;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.placeholder.PlaceholderResolver;

public interface PropertyFactory extends ValueFactory<String>, Iterable<String>, PlaceholderResolver {
	Iterator<String> iterator();

	default String resolvePlaceholder(String placeholderName) {
		return getString(placeholderName);
	}

	default Stream<String> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default Stream<String> stream(String pattern, StringMatcher keyMatcher) {
		Assert.requiredArgument(pattern != null, "pattern");
		Assert.requiredArgument(keyMatcher != null, "keyMatcher");
		return stream().filter((t) -> StringMatchers.match(keyMatcher, pattern, t));
	}

	/**
	 * 通过前缀过滤的流
	 * 
	 * @param prefix
	 * @return key是截取过后的结果
	 */
	default Stream<Pair<String, Value>> streamByPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		return stream().filter((k) -> k.length() > prefix.length() && k.startsWith(prefix))
				.map((k) -> new Pair<>(k.substring(prefix.length()), getValue(k)));
	}
}
