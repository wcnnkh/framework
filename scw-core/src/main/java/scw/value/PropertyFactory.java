package scw.value;

import java.util.Iterator;

import scw.util.placeholder.PlaceholderResolver;

public interface PropertyFactory extends ValueFactory<String>,
		Iterable<String>, PlaceholderResolver {
	Iterator<String> iterator();

	default String resolvePlaceholder(String placeholderName) {
		return getString(placeholderName);
	}
}
