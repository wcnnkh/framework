package scw.value;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.util.StringMatcher;
import scw.util.StringMatchers;
import scw.util.placeholder.PlaceholderResolver;

public interface PropertyFactory extends ValueFactory<String>, Iterable<String>, PlaceholderResolver {
	Iterator<String> iterator();

	default String resolvePlaceholder(String placeholderName) {
		return getString(placeholderName);
	}

	default Stream<String> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default Stream<String> stream(String pattern, StringMatcher keyMatcher) {
		return stream().filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				return StringMatchers.match(keyMatcher, pattern, t);
			}
		});
	}
}
