package run.soeasy.framework.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.CharSequenceSplitIterator;
import run.soeasy.framework.core.domain.CharSequenceTemplate;
import run.soeasy.framework.core.domain.Range;

@UtilityClass
public class StringUtils {
	public static final String[] EMPTY_ARRAY = new String[0];

	public static int count(CharSequence charSequence, CharSequence target) {
		return count(charSequence, 0, target);
	}

	public static int count(CharSequence charSequence, int beginIndex, CharSequence target) {
		if (charSequence == null) {
			return 0;
		}

		return count(charSequence, beginIndex, charSequence.length(), target);
	}

	public static int count(CharSequence charSequence, int beginIndex, int endIndex, CharSequence target) {
		if (charSequence == null || target == null) {
			return 0;
		}

		int count = 0;
		int index = indexOf(charSequence, target, beginIndex, endIndex);
		while (index != -1) {
			count++;
			index = indexOf(charSequence, target, index + target.length(), endIndex);
		}
		return count;
	}

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}

		String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	public static boolean equals(final CharSequence cs, final boolean ignoreCase, final int thisStart,
			final CharSequence substring, final int start, final int length) {
		if (cs == null || substring == null) {
			return cs == substring;
		}

		if (cs instanceof String && substring instanceof String) {
			return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
		}
		int index1 = thisStart;
		int index2 = start;
		int tmpLen = length;

		// Extract these first so we detect NPEs the same as the
		// java.lang.String version
		final int srcLen = cs.length() - thisStart;
		final int otherLen = substring.length() - start;

		// Check for invalid parameters
		if (thisStart < 0 || start < 0 || length < 0) {
			return false;
		}

		// Check that the regions are long enough
		if (srcLen < length || otherLen < length) {
			return false;
		}

		while (tmpLen-- > 0) {
			final char c1 = cs.charAt(index1++);
			final char c2 = substring.charAt(index2++);

			if (c1 == c2) {
				continue;
			}

			if (!ignoreCase) {
				return false;
			}

			// The same check as in String.regionMatches():
			if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
					&& Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(CharSequence a, CharSequence b) {
		return equals(a, b, false);
	}

	public static boolean equals(CharSequence a, CharSequence b, boolean ignoreCase) {
		return equals(a, ignoreCase, 0, b, 0, a.length());
	}

	public static boolean hasText(CharSequence value) {
		if (value == null) {
			return false;
		}

		int len = value.length();
		if (len == 0) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasText(CharSequence value, int fromIndex, int endIndex) {
		if (fromIndex == endIndex) {
			return false;
		}

		if (isEmpty(value)) {
			return false;
		}

		for (int i = fromIndex, end = Math.min(value.length(), endIndex); i < end; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static int indexOf(char[] source, char[] target) {
		return indexOf(source, target, 0);
	}

	public static Range<Integer> indexOf(char[] source, char[] prefix, char[] suffix, int fromIndex, int endIndex) {
		if (source == null || prefix == null || suffix == null) {
			return null;
		}

		int begin = indexOf(source, prefix, 0, endIndex);
		if (begin == -1) {
			return null;
		}

		int end = indexOf(source, suffix, begin + prefix.length, endIndex);
		if (end == -1) {
			return null;
		}

		int tempBegin = begin;
		int tempEnd = end;
		while (true) {// 重复查找是否存在嵌套,直到找到最外层的{suffix}
			int nestingLevel = 0;// 嵌套了多少层
			while (true) {
				int index = indexOf(source, prefix, tempBegin + prefix.length, tempEnd);
				if (index == -1) {
					break;
				}
				nestingLevel++;
				tempBegin = index;
			}

			if (nestingLevel == 0) {
				break;
			}

			// prefix嵌套了多少层就将suffix向外移多少层
			for (int i = 0; i < nestingLevel; i++) {
				tempEnd = indexOf(source, suffix, tempEnd + suffix.length, endIndex);
				if (tempEnd == -1) {// 两边的符号嵌套层级不一至
					return null;
				}
			}
		}
		return Range.closed(begin, tempEnd);
	}

	public static int indexOf(char[] source, char[] target, int fromIndex) {
		return indexOf(source, target, fromIndex, source.length);
	}

	public static int indexOf(char[] source, char[] target, int fromIndex, int endIndex) {
		int index = indexOf(source, fromIndex, endIndex - fromIndex, target, 0, target.length, 0);
		return index == -1 ? -1 : index + fromIndex;
	}

	public static int indexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset,
			int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first)
					;
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++)
					;

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public static int indexOf(CharSequence source, CharSequence target) {
		return indexOf(source, target, 0);
	}

	public static Range<Integer> indexOf(CharSequence text, CharSequence prefix, CharSequence suffix) {
		return indexOf(text, prefix, suffix, 0);
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	// ---------------------------------------------------------------------

	public static Range<Integer> indexOf(CharSequence source, CharSequence prefix, CharSequence suffix, int fromIndex) {
		if (source == null) {
			return null;
		}

		return indexOf(source, prefix, suffix, fromIndex, source.length());
	}

	public static Range<Integer> indexOf(CharSequence source, CharSequence prefix, CharSequence suffix, int fromIndex,
			int endIndex) {
		if (source == null || prefix == null || suffix == null) {
			return null;
		}

		int begin = indexOf(source, prefix, fromIndex, endIndex);
		if (begin == -1) {
			return null;
		}

		int prefixLength = prefix.length();
		int end = indexOf(source, suffix, begin + prefixLength, endIndex);
		if (end == -1) {
			return null;
		}

		int suffixLength = suffix.length();
		int tempBegin = begin;
		int tempEnd = end;
		while (true) {// 重复查找是否存在嵌套,直到找到最外层的{suffix}
			int nestingLevel = 0;// 嵌套了多少层
			while (true) {
				int index = indexOf(source, prefix, tempBegin + prefixLength, tempEnd);
				if (index == -1) {
					break;
				}
				nestingLevel++;
				tempBegin = index;
			}

			if (nestingLevel == 0) {
				break;
			}

			// prefix嵌套了多少层就将suffix向外移多少层
			for (int i = 0; i < nestingLevel; i++) {
				tempEnd = indexOf(source, suffix, tempEnd + suffixLength, endIndex);
				if (tempEnd == -1) {// 两边的符号嵌套层级不一至
					return null;
				}
			}
		}
		return Range.closed(begin, tempEnd);
	}

	public static int indexOf(CharSequence source, CharSequence target, int fromIndex) {
		if (source == null) {
			return -1;
		}
		return indexOf(source, target, fromIndex, source.length());
	}

	public static int indexOf(CharSequence source, CharSequence target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}
		int index = indexOf(source, fromIndex, endIndex - fromIndex, target, 0, target.length(), 0);
		return index == -1 ? -1 : index + fromIndex;
	}

	public static int indexOf(CharSequence source, int sourceOffset, int sourceCount, CharSequence target,
			int targetOffset, int targetCount, int fromIndex) {
		if (source == null || target == null) {
			return -1;
		}

		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target.charAt(targetOffset);
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source.charAt(i) != first) {
				while (++i <= max && source.charAt(i) != first)
					;
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source.charAt(j) == target.charAt(k); j++, k++)
					;

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public static boolean isEmpty(CharSequence value) {
		return value == null || value.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence value) {
		return !isEmpty(value);
	}

	public static int lastIndexOf(char[] source, char[] target) {
		if (source == null) {
			return -1;
		}

		return lastIndexOf(source, target, source.length);
	}

	public static int lastIndexOf(char[] source, char[] target, int fromIndex) {
		return lastIndexOf(source, target, fromIndex, 0);
	}

	public static int lastIndexOf(char[] source, char[] target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}

		int sourceCount = Math.min(fromIndex, source.length) - endIndex;
		int index = lastIndexOf(source, endIndex, sourceCount, target, 0, target.length, sourceCount);
		return index == -1 ? -1 : index + endIndex;
	}

	public static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset,
			int targetCount, int fromIndex) {
		if (source == null || target == null) {
			return -1;
		}

		/*
		 * Check arguments; return immediately where possible. For consistency, don't
		 * check for null str.
		 */
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true) {
			while (i >= min && source[i] != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source[j--] != target[k--]) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	public static int lastIndexOf(CharSequence source, CharSequence target) {
		if (source == null || target == null) {
			return -1;
		}

		return lastIndexOf(source, target, source.length());
	}

	public static int lastIndexOf(CharSequence source, CharSequence target, int fromIndex) {
		return lastIndexOf(source, target, fromIndex, 0);
	}

	public static int lastIndexOf(CharSequence source, CharSequence target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}

		int sourceCount = Math.min(fromIndex, source.length()) - endIndex;
		int index = lastIndexOf(source, endIndex, sourceCount, target, 0, target.length(), sourceCount);
		return index == -1 ? -1 : index + endIndex;
	}

	public static int lastIndexOf(CharSequence source, int sourceOffset, int sourceCount, CharSequence target,
			int targetOffset, int targetCount, int fromIndex) {
		if (source == null || target == null) {
			return -1;
		}

		/*
		 * Check arguments; return immediately where possible. For consistency, don't
		 * check for null str.
		 */
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target.charAt(strLastIndex);
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true) {
			while (i >= min && source.charAt(i) != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source.charAt(j--) != target.charAt(k--)) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	public static Elements<CharSequenceTemplate> split(CharSequence charSequence, boolean trimTokens,
			boolean ignoreEmptyTokens, CharSequence... filters) {
		return split(charSequence, filters).map((s) -> trimTokens ? (s == null ? s : s.trim()) : s)
				.filter((s) -> (ignoreEmptyTokens ? StringUtils.isNotEmpty(s) : true));
	}

	public static Elements<CharSequenceTemplate> split(CharSequence charSequence, CharSequence... filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), Arrays.asList(filters));
	}

	public static Elements<CharSequenceTemplate> split(CharSequence charSequence,
			Collection<? extends CharSequence> filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), filters);
	}

	public static Elements<CharSequenceTemplate> split(CharSequence charSequence, int beginIndex, int endIndex,
			Collection<? extends CharSequence> filters) {
		if (StringUtils.isEmpty(charSequence)) {
			return Elements.empty();
		}

		boolean find = false;
		for (CharSequence filter : filters) {
			if (indexOf(charSequence, filter, beginIndex, endIndex) != -1) {
				find = true;
				break;
			}
		}

		if (!find) {
			return Elements.singleton(new CharSequenceTemplate(charSequence));
		}
		return Elements.of(() -> new CharSequenceSplitIterator(charSequence, filters, beginIndex, endIndex));
	}

	public static String[] splitToArray(CharSequence charSequence, boolean trimTokens, boolean ignoreEmptyTokens,
			CharSequence... filters) {
		return split(charSequence, trimTokens, ignoreEmptyTokens, filters).map((s) -> s == null ? null : s.toString())
				.toArray(String[]::new);
	}

	public static String[] splitToArray(CharSequence charSequence, CharSequence... filters) {
		return splitToArray(charSequence, true, true, filters);
	}

	public static boolean startsWith(String text, String prefix, boolean ignoreCase) {
		return startsWith(text, prefix, 0, ignoreCase);
	}

	public static boolean startsWith(String text, String prefix, int toOffset, boolean ignoreCase) {
		if (ignoreCase) {
			int to = toOffset;
			int po = 0;
			int pc = prefix.length();
			// Note: toffset might be near -1&gt;&gt;&gt;1.
			if ((toOffset < 0) || (toOffset > text.length() - pc)) {
				return false;
			}
			while (--pc >= 0) {
				if (Character.toLowerCase(text.charAt(to++)) != Character.toLowerCase(prefix.charAt(po++))) {
					return false;
				}
			}
			return true;
		} else {
			return text.startsWith(prefix);
		}
	}

	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	public static Elements<String> tokenize(String text, String delimiters) {
		if (StringUtils.isEmpty(text)) {
			return Elements.empty();
		}

		return tokenize(new StringTokenizer(text, delimiters));
	}

	public static Elements<String> tokenize(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {
		return tokenize(str, delimiters).map((s) -> trimTokens ? (s == null ? s : s.trim()) : s)
				.filter((s) -> (ignoreEmptyTokens ? StringUtils.isNotEmpty(s) : true));
	}

	public static Elements<String> tokenize(StringTokenizer tokenizer) {
		if (tokenizer == null) {
			return Elements.empty();
		}

		return Elements.of(() -> new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return tokenizer.hasMoreTokens();
			}

			@Override
			public String next() {
				return tokenizer.nextToken();
			}
		});
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer. Trims
	 * tokens and omits empty tokens.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of delimiter
	 * characters. Each of those characters can be used to separate tokens. A
	 * delimiter is always a single character; for multi-character delimiters,
	 * consider using {@code splitToArray}
	 * 
	 * @param str        the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String (each of
	 *                   those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 * @see #splitToArray
	 */
	public static String[] tokenizeToArray(String str, String delimiters) {
		return tokenizeToArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of delimiter
	 * characters. Each of those characters can be used to separate tokens. A
	 * delimiter is always a single character; for multi-character delimiters,
	 * consider using {@code splitToArray}
	 * 
	 * @param str               the String to tokenize
	 * @param delimiters        the delimiter characters, assembled as String (each
	 *                          of those characters is individually considered as
	 *                          delimiter)
	 * @param trimTokens        trim the tokens via String's {@code trim}
	 * @param ignoreEmptyTokens omit empty tokens from the result array (only
	 *                          applies to tokens that are empty after trimming;
	 *                          StringTokenizer will not consider subsequent
	 *                          delimiters as token in the first place).
	 * @return an array of the tokens ({@code null} if the input String was
	 *         {@code null})
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 * @see #splitToArray
	 */
	public static String[] tokenizeToArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {
		return tokenize(str, delimiters, trimTokens, ignoreEmptyTokens).toArray(String[]::new);
	}

	/**
	 * 对字符串进行转义
	 * 
	 * @param text
	 * @param chars 要转义的字符
	 * @return
	 */
	public static String transferredMeaning(String text, char... chars) {
		if (isEmpty(text)) {
			return text;
		}

		int len = text.length();
		char[] values = new char[len * 2];
		int vIndex = 0;
		for (int index = 0; index < len; index++) {
			char v = text.charAt(index);
			for (char c : chars) {
				if (c == v) {
					values[vIndex++] = '\\';
					break;
				}
			}
			values[vIndex++] = v;
		}
		return new String(values, 0, vIndex);
	}
}
