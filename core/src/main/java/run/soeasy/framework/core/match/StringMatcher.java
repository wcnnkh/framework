package run.soeasy.framework.core.match;

import java.util.Comparator;

public interface StringMatcher extends Comparator<String> {
	public static StringMatcher identity() {
		return IdentityMatcher.INSTANCE;
	}

	/**
	 * 固定前缀匹配器，严格基于 text.startsWith(pattern) 实现。 模式本身就是完整前缀，不支持任何通配符或特殊字符。
	 * 
	 * @return
	 */
	public static StringMatcher prefix() {
		return PrefixMatcher.DEFAULT;
	}

	/**
	 * 前缀匹配器
	 * 
	 * @param ignoreCase 是否忽略大小写
	 * @return
	 */
	public static StringMatcher prefix(boolean ignoreCase) {
		return ignoreCase ? PrefixMatcher.IGNORE_CASE : PrefixMatcher.DEFAULT;
	}

	/**
	 * 标准通配符匹配器，支持： - ?：匹配任意单个字符 - *：匹配任意数量（包括0个）的任意字符序列
	 * 
	 * @return
	 */
	public static StringMatcher wildcard() {
		return WildcardMatcher.INSTANCE;
	}

	/**
	 * 此是否符合此匹配模式
	 * 
	 * @param text
	 * @return
	 */
	boolean isPattern(String text);

	/**
	 * 是否匹配
	 * 
	 * @param pattern
	 * @param text
	 * @return
	 */
	boolean match(String pattern, String text);

	/**
	 * Given a pattern and a full text, determine the pattern-mapped part.
	 * <p>
	 * This method is supposed to find out which part of the text is matched
	 * dynamically through an actual pattern, that is, it strips off a statically
	 * defined leading text from the given full text, returning only the actually
	 * pattern-matched part of the text.
	 * <p>
	 * For example: For "myroot/*.html" as pattern and "myroot/myfile.html" as full
	 * text, this method should return "myfile.html". The detailed determination
	 * rules are specified to this PathMatcher's matching strategy.
	 * <p>
	 * A simple implementation may return the given full text as-is in case of an
	 * actual pattern, and the empty String in case of the pattern not containing
	 * any dynamic parts (i.e. the {@code pattern} parameter being a static text
	 * that wouldn't qualify as an actual {@link #isPattern pattern}). A
	 * sophisticated implementation will differentiate between the static parts and
	 * the dynamic parts of the given text pattern.
	 * 
	 * @param pattern the text pattern
	 * @param text    the full text to introspect
	 * @return the pattern-mapped part of the given {@code text} (never
	 *         {@code null})
	 */
	String extractWithinPattern(String pattern, String text);

	@Override
	default int compare(String o1, String o2) {
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
		return o1.compareTo(o2);
	}

}
