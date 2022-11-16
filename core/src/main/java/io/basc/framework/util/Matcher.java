package io.basc.framework.util;

import java.util.Comparator;

public interface Matcher<T> extends Comparator<T> {
	/**
	 * Does the given {@code text} represent a pattern that can be matched by an
	 * implementation of this interface?
	 * <p>
	 * If the return value is {@code false}, then the {@link #match} method does not
	 * have to be used because direct equality comparisons on the static path
	 * Strings will lead to the same result.
	 * 
	 * @param path the path String to check
	 * @return {@code true} if the given {@code text} represents a pattern
	 */
	boolean isPattern(T text);

	/**
	 * Match the given {@code path} against the given {@code pattern}, according to
	 * this Matcher's matching strategy.
	 * 
	 * @param pattern the pattern to match against
	 * @param path    the path String to test
	 * @return {@code true} if the supplied {@code text} matched, {@code false} if
	 *         it didn't
	 */
	boolean match(T pattern, T text);

	@Override
	default int compare(T o1, T o2) {
		if (isPattern(o1) && isPattern(o2)) {
			if (match(o1, o2)) {
				return 1;
			} else if (match(o2, o1)) {
				return -1;
			} else {
				return -1;
			}
		} else if (isPattern(o1)) {
			return 1;
		} else if (isPattern(o2)) {
			return -1;
		}
		return o1.equals(o1) ? 0 : -1;
	}
}
