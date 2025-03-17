package run.soeasy.framework.util.match;

/**
 * 字符串匹配
 * 
 * @author wcnnkh
 *
 */
public interface StringMatcher extends Matcher<String> {
	/**
	 * Does the given {@code text} represent a pattern that can be matched by an
	 * implementation of this interface?
	 * <p>
	 * If the return value is {@code false}, then the {@link #match} method does not
	 * have to be used because direct equality comparisons on the static path
	 * Strings will lead to the same result.
	 * 
	 * @param text the path String to check
	 * @return {@code true} if the given {@code text} represents a pattern
	 */
	@Override
	boolean isPattern(String text);

	/**
	 * Match the given {@code path} against the given {@code pattern}, according to
	 * this Matcher's matching strategy.
	 * 
	 * @param pattern the pattern to match against
	 * @param text    the path String to test
	 * @return {@code true} if the supplied {@code text} matched, {@code false} if
	 *         it didn't
	 */
	@Override
	boolean match(String pattern, String text);

	default StringMatcher split(String split) {
		return new SplitStringMatcher(this, split);
	}
}
