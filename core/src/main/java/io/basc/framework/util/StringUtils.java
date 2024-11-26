package io.basc.framework.util;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.function.IntPredicate;

import io.basc.framework.util.collect.CollectionUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import lombok.NonNull;

public final class StringUtils {
	private static final String CURRENT_PATH = ".";

	/**
	 * 默认的分割符 [" ", ",", ";", "、"]
	 */
	private static final String[] DEFAULT_SEPARATOR = new String[] { " ", ",", ";", "、" };

	public static final String[] EMPTY_ARRAY = new String[0];

	private static final char EXTENSION_SEPARATOR = '.';

	private static final String FOLDER_SEPARATOR = "/";

	private static final String TOP_PATH = "..";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	/**
	 * Apply the given relative path to the given path, assuming standard Java
	 * folder separation (i.e. "/" separators).
	 * 
	 * @param path         the path to start from (usually a full file path)
	 * @param relativePath the relative path to apply (relative to the full file
	 *                     path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		} else {
			return relativePath;
		}
	}

	/**
	 * Convenience method to return a String array as a CSV String. E.g. useful for
	 * {@code toString()} implementations.
	 * 
	 * @param arr the array to display
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV) String.
	 * E.g. useful for {@code toString()} implementations.
	 * 
	 * @param arr   the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (ObjectUtils.isEmpty(arr)) {
			return "";
		}
		if (arr.length == 1) {
			return ObjectUtils.toString(arr[0]);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Capitalize a {@code String}, changing the first letter to upper case as per
	 * {@link Character#toUpperCase(char)}. No other letters are changed.
	 * 
	 * @param str the {@code String} to capitalize
	 * @return the capitalized {@code String}
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (isEmpty(str)) {
			return str;
		}

		char baseChar = str.charAt(0);
		char updatedChar;
		if (capitalize) {
			updatedChar = Character.toUpperCase(baseChar);
		} else {
			updatedChar = Character.toLowerCase(baseChar);
		}
		if (baseChar == updatedChar) {
			return str;
		}

		char[] chars = str.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and inner simple
	 * dots.
	 * <p>
	 * The result is convenient for path comparison. For other uses, notice that
	 * Windows separators ("\") are replaced by simple slashes.
	 * 
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {
		if (path == null) {
			return null;
		}
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		String suffix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}
		if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
			prefix = prefix + FOLDER_SEPARATOR;
			pathToUse = pathToUse.substring(1);
		}
		if (pathToUse.endsWith(FOLDER_SEPARATOR)) {
			suffix = suffix + FOLDER_SEPARATOR;
		}

		String[] pathArray = splitToArray(pathToUse, false, true, FOLDER_SEPARATOR);
		List<String> pathElements = new LinkedList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			} else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			} else {
				if (tops > 0) {
					// Merging path element with element corresponding to top
					// path.
					tops--;
				} else {
					// Normal path element found.
					pathElements.add(0, element);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		if (pathElements.isEmpty()) {
			if (prefix.endsWith(suffix)) {
				return prefix;
			}
			return prefix + suffix;
		}

		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR) + suffix;
	}

	/**
	 * Convenience method to return a Collection as a CSV String. E.g. useful for
	 * {@code toString()} implementations.
	 * 
	 * @param coll the Collection to display
	 * @return the delimited String
	 */
	public static String collectionToCommaDelimitedString(Collection<?> coll) {
		return collectionToDelimitedString(coll, ",");
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV) String.
	 * E.g. useful for {@code toString()} implementations.
	 * 
	 * @param coll  the Collection to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV) String.
	 * E.g. useful for {@code toString()} implementations.
	 * 
	 * @param coll   the Collection to display
	 * @param delim  the delimiter to use (probably a ",")
	 * @param prefix the String to start each element with
	 * @param suffix the String to end each element with
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
		if (CollectionUtils.isEmpty(coll)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	/**
	 * 把不足的地方用指定字符填充
	 * 
	 * @param text
	 * @param complemented
	 * @param length
	 * @return
	 */
	public static String complemented(String text, char complemented, int length) {
		Assert.isTrue(length >= text.length(),
				"The length of text[" + text + "] exceeds the target length[" + length + "]");
		if (length == text.length()) {
			return text;
		} else {
			CharBuffer charBuffer = CharBuffer.allocate(length);
			for (int i = 0; i < length - text.length(); i++) {
				charBuffer.put(complemented);
			}
			charBuffer.put(text);
			return new String(charBuffer.array());
		}
	}

	/**
	 * Concatenate the given String arrays into one, with overlapping array elements
	 * included twice.
	 * <p>
	 * The order of elements in the original arrays is preserved.
	 * 
	 * @param array1 the first array (can be {@code null})
	 * @param array2 the second array (can be {@code null})
	 * @return the new array ({@code null} if both given arrays were {@code null})
	 */
	public static String[] concatenateStringArrays(String[] array1, String[] array2) {
		if (ObjectUtils.isEmpty(array1)) {
			return array2;
		}
		if (ObjectUtils.isEmpty(array2)) {
			return array1;
		}
		String[] newArr = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, newArr, 0, array1.length);
		System.arraycopy(array2, 0, newArr, array1.length, array2.length);
		return newArr;
	}

	public static boolean contains(String text, String index, boolean ignoreCase) {
		if (text == null || index == null) {
			return text == index;
		}

		if (ignoreCase) {
			return text.toLowerCase().contains(index.toLowerCase());
		} else {
			return text.contains(index);
		}
	}

	/**
	 * 判断字符串中是否存在中文
	 * 
	 * @param charSequence
	 * @return
	 */
	public static boolean containsChinese(CharSequence charSequence) {
		if (charSequence == null || charSequence.length() == 0) {
			return false;
		}

		for (int i = 0; i < charSequence.length(); i++) {
			if (isChinese(charSequence.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given CharSequence contains any whitespace characters.
	 * 
	 * @param str the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not empty and contains at least 1
	 *         whitespace character
	 * @see Character#isWhitespace
	 */
	public static boolean containsWhitespace(CharSequence str) {
		if (isEmpty(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 把unicode 转成中文
	 * 
	 * @return
	 */
	public static String convertUnicode(String ori) {
		char aChar;
		int len = ori.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = ori.charAt(x++);
			if (aChar == '\\') {
				aChar = ori.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = ori.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);

		}
		return outBuffer.toString();
	}

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

	/**
	 * Delete all occurrences of the given substring.
	 * 
	 * @param inString the original String
	 * @param pattern  the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given String.
	 * 
	 * @param inString      the original String
	 * @param charsToDelete a set of characters to delete. E.g. "az\n" will delete
	 *                      'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (isEmpty(inString) || isEmpty(charsToDelete)) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Test if the given String ends with the specified suffix, ignoring upper/lower
	 * case.
	 * 
	 * @param str    the String to check
	 * @param suffix the suffix to look for
	 * @see java.lang.String#endsWith
	 */
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

	public static boolean equals(String a, String b) {
		return equals(a, b, false);
	}

	public static boolean equals(String a, String b, boolean ignoreCase) {
		if (a == null || b == null) {
			return a == b;
		}

		if (a.length() == 0 || b.length() == 0) {
			return a.length() == b.length();
		}

		return ignoreCase ? a.equalsIgnoreCase(b) : a.equals(b);
	}

	public static boolean equalsAny(final CharSequence cs, final boolean ignoreCase, final CharSequence... substring) {
		if (substring == null) {
			return false;
		}

		for (CharSequence sub : substring) {
			if (equals(cs, sub, ignoreCase)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsAny(final CharSequence cs, final int thisStart, final boolean ignoreCase,
			final int start, final int length, final CharSequence... substring) {
		if (substring == null) {
			return false;
		}

		for (CharSequence sub : substring) {
			if (equals(cs, ignoreCase, thisStart, sub, start, length)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 任意一个匹配成功就为true
	 * 
	 * @return
	 */
	public static boolean equalsAny(String a, boolean ignoreCase, String... array) {
		if (array == null) {
			return false;
		}

		for (String b : array) {
			if (equals(a, b, ignoreCase)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsAny(String a, String... array) {
		return equalsAny(a, false, array);
	}

	/**
	 * 过滤
	 * 
	 * @param source
	 * @param filter
	 * @return
	 */
	public static String filter(CharSequence source, IntPredicate filter) {
		if (source == null) {
			return null;
		}

		if (source.length() == 0 || filter == null) {
			return source.toString();
		}

		char[] chars = new char[source.length()];
		int pos = 0;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (filter.test(c)) {
				chars[pos++] = c;
			}
		}
		return pos == 0 ? null : new String(chars, 0, pos);
	}

	/**
	 * Extract the filename from the given path, e.g. "mypath/myfile.txt" --&gt;
	 * "myfile.txt".
	 * 
	 * @param path the file path (may be {@code null})
	 * @return the extracted filename, or {@code null} if none
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	// ---------------------------------------------------------------------

	/**
	 * Extract the filename extension from the given path, e.g. "mypath/myfile.txt"
	 * -&gt; "txt".
	 * 
	 * @param path the file path (may be {@code null})
	 * @return the extracted filename extension, or {@code null} if none
	 */
	public static String getFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return null;
		}
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return null;
		}
		return path.substring(extIndex + 1);
	}

	/**
	 * Check whether the given CharSequence has actual text. More specifically,
	 * returns {@code true} if the string not {@code null}, its length is greater
	 * than 0, and it contains at least one non-whitespace character.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * 
	 * @param value the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not {@code null}, its length is
	 *         greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
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

	/**
	 * all has text
	 * 
	 * @param values
	 * @return
	 */
	public static boolean hasTextAll(CharSequence... values) {
		if (values == null || values.length == 0) {
			return false;
		}

		for (CharSequence s : values) {
			if (!hasText(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 驼峰命名大写替换
	 * 
	 * @param humpNaming
	 * @param replacement
	 * @return
	 */
	public static String humpNamingReplacement(String humpNaming, String replacement) {
		int len = humpNaming.length();
		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			char c = humpNaming.charAt(i);
			if (Character.isUpperCase(c)) {// 如果是大写的
				if (i != 0) {
					sb.append(replacement);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static int indexOf(char[] source, char[] target) {
		return indexOf(source, target, 0);
	}

	/**
	 * 从左边开始获取组合第一次出现的位置, prefix和suffix必须是成对出现的，允许嵌套
	 * 
	 * @param source
	 * @param prefix
	 * @param suffix
	 * @param fromIndex
	 * @param endIndex
	 * @return
	 */
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

	/**
	 * Code shared by String and StringBuffer to do searches. The source is the
	 * character array being searched, and the target is the string being searched
	 * for.
	 *
	 * @param source       the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount  count of the source string.
	 * @param target       the characters being searched for.
	 * @param targetOffset offset of the target string.
	 * @param targetCount  count of the target string.
	 * @param fromIndex    the index to begin searching from.
	 */
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

	/**
	 * 从左边开始获取字符串组合第一次出现的位置, prefix和suffix必须是成对出现的，允许嵌套
	 * 
	 * @param text
	 * @param prefix
	 * @param suffix
	 * @return
	 */
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

	public static boolean isAllEmpty(CharSequence... charSequences) {
		if (charSequences == null || charSequences.length == 0) {
			return true;
		}

		for (CharSequence charSequence : charSequences) {
			if (isNotEmpty(charSequence)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * any is empty
	 * 
	 * @param values
	 * @return
	 */
	public static boolean isAnyEmpty(CharSequence... values) {
		if (values == null || values.length == 0) {
			return true;
		}

		for (CharSequence s : values) {
			if (isEmpty(s)) {
				return true;
			}
		}
		return false;
	}

	// 根据Unicode编码判断中文汉字和符号
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
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

	/**
	 * Code shared by String and StringBuffer to do searches. The source is the
	 * character array being searched, and the target is the string being searched
	 * for.
	 *
	 * @param source       the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount  count of the source string.
	 * @param target       the characters being searched for.
	 * @param targetOffset offset of the target string.
	 * @param targetCount  count of the target string.
	 * @param fromIndex    the index to begin searching from.
	 */
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

	public static char[] mergeCharArray(char[]... chars) {
		StringBuilder sb = new StringBuilder();
		for (char[] cs : chars) {
			sb.append(cs);
		}
		return sb.toString().toCharArray();
	}

	/**
	 * 合并多个路径
	 * 
	 * @param paths
	 * @return
	 */
	public static String mergePaths(@NonNull Collection<String> paths, PlaceholderFormat placeholderFormat) {
		StringBuilder sb = new StringBuilder();
		for (String path : paths) {
			if (StringUtils.isEmpty(path)) {
				continue;
			}

			if (placeholderFormat != null) {
				path = placeholderFormat.replacePlaceholders(path);
			}

			path = StringUtils.cleanPath(path);
			if (sb.length() != 0 && !path.startsWith(FOLDER_SEPARATOR)) {
				sb.append(FOLDER_SEPARATOR);
			}
			sb.append(path);
		}
		return cleanPath(sb.toString());
	}

	public static String mergePaths(String... paths) {
		return mergePaths(Arrays.asList(paths), null);
	}

	/**
	 * Merge the given String arrays into one, with overlapping array elements only
	 * included once.
	 * <p>
	 * The order of elements in the original arrays is preserved (with the exception
	 * of overlapping elements, which are only included on their first occurrence).
	 * 
	 * @param array1 the first array (can be {@code null})
	 * @param array2 the second array (can be {@code null})
	 * @return the new array ({@code null} if both given arrays were {@code null})
	 */
	public static String[] mergeStringArrays(String[] array1, String[] array2) {
		if (ObjectUtils.isEmpty(array1)) {
			return array2;
		}
		if (ObjectUtils.isEmpty(array2)) {
			return array1;
		}
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList(array1));
		for (String str : array2) {
			if (!result.contains(str)) {
				result.add(str);
			}
		}
		return toStringArray(result);
	}

	public static Range<String> parseKV(String text, String separator) {
		int index = text.indexOf(separator);
		if (index == -1) {
			return null;
		}

		return Range.closed(text.substring(0, index), text.substring(index + separator.length()));
	}

	/**
	 * Compare two paths after normalization of them.
	 * 
	 * @param path1 first path for comparison
	 * @param path2 second path for comparison
	 * @return whether the two paths are equivalent after normalization
	 */
	public static boolean pathEquals(String path1, String path2) {
		return cleanPath(path1).equals(cleanPath(path2));
	}

	/**
	 * Quote the given String with single quotes.
	 * 
	 * @param str the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or {@code null} if the input
	 *         was {@code null}
	 */
	public static String quote(String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes if it is a String;
	 * keeping the Object as-is else.
	 * 
	 * @param obj the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or the input object as-is if
	 *         not a String
	 */
	public static Object quoteIfString(Object obj) {
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	public static String removeChar(String text, char remove) {
		int len = text.length();
		if (len == 0) {
			return text;
		}

		char[] cs = new char[len];
		int index = 0;
		for (int i = 0; i < len; i++) {
			char v = text.charAt(i);
			if (v == remove) {
				continue;
			}

			cs[index++] = v;
		}
		return new String(cs, 0, index);
	}

	/**
	 * Remove duplicate Strings from the given array. Also sorts the array, as it
	 * uses a TreeSet.
	 * 
	 * @param array the String array
	 * @return an array without duplicates, in natural sort order
	 */
	public static String[] removeDuplicateStrings(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return array;
		}
		Set<String> set = new TreeSet<String>();
		for (String element : array) {
			set.add(element);
		}
		return toStringArray(set);
	}

	public static void replace(char[] chars, char replace, char newChar) {
		if (ArrayUtils.isEmpty(chars)) {
			return;
		}

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == replace) {
				chars[i] = newChar;
			}
		}
	}

	public static String replace(String text, char replace, char newChar) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		char[] chars = text.toCharArray();
		replace(chars, replace, newChar);
		return new String(chars);
	}

	/**
	 * Replace all occurences of a substring within a string with another string.
	 * 
	 * @param inString   String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (isEmpty(inString) || isEmpty(oldPattern) || newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	/**
	 * 颠倒字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String reversed(String str) {
		if (isEmpty(str)) {
			return str;
		}

		return new String(reversedCharArray(str.toCharArray()));
	}

	public static char[] reversedCharArray(char[] array) {
		if (array == null) {
			return array;
		}

		char[] newArray = new char[array.length];
		int index = 0;
		for (int i = newArray.length - 1; i >= 0; i--, index++) {
			newArray[index] = array[i];
		}
		return newArray;
	}

	/**
	 * Turn given source String array into sorted array.
	 * 
	 * @param array the source array
	 * @return the sorted array (never {@code null})
	 */
	public static String[] sortStringArray(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return EMPTY_ARRAY;
		}
		Arrays.sort(array);
		return array;
	}

	/**
	 * @see StringUtils#DEFAULT_SEPARATOR
	 * @param charSequence
	 * @return
	 */
	public static Elements<CharSequenceSplitSegment> split(CharSequence charSequence) {
		return split(charSequence, DEFAULT_SEPARATOR);
	}

	public static Elements<CharSequenceSplitSegment> split(CharSequence charSequence, boolean trimTokens,
			boolean ignoreEmptyTokens, CharSequence... filters) {
		return split(charSequence, filters).map((s) -> trimTokens ? (s == null ? s : s.trim()) : s)
				.filter((s) -> (ignoreEmptyTokens ? StringUtils.isNotEmpty(s) : true));
	}

	public static Elements<CharSequenceSplitSegment> split(CharSequence charSequence, CharSequence... filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), Arrays.asList(filters));
	}

	public static Elements<CharSequenceSplitSegment> split(CharSequence charSequence,
			Collection<? extends CharSequence> filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), filters);
	}

	public static Elements<CharSequenceSplitSegment> split(CharSequence charSequence, int beginIndex, int endIndex,
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
			return Elements.singleton(new CharSequenceSplitSegment(charSequence));
		}
		return Elements.of(() -> new CharSequenceSplitIterator(charSequence, filters, beginIndex, endIndex));
	}

	public static String[] splitToArray(CharSequence charSequence) {
		return splitToArray(charSequence, DEFAULT_SEPARATOR);
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

	/**
	 * Test if the given String starts with the specified prefix, ignoring
	 * upper/lower case.
	 * 
	 * @param str    the String to check
	 * @param prefix the prefix to look for
	 * @see java.lang.String#startsWith
	 */
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

	/**
	 * Strip the filename extension from the given path, e.g. "mypath/myfile.txt"
	 * -&gt; "mypath/myfile".
	 * 
	 * @param path the file path (may be {@code null})
	 * @return the path with stripped filename extension, or {@code null} if none
	 */
	public static String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return path;
		}
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return path;
		}
		return path.substring(0, extIndex);
	}

	/**
	 * Test whether the given string matches the given substring at the given index.
	 * 
	 * @param str       the original string (or StringBuilder)
	 * @param index     the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 */
	public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
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

	public static String toLowerCase(String text, int begin, int end) {
		char[] chars = text.toCharArray();
		for (int i = begin; i < end; i++) {
			chars[i] = Character.toLowerCase(chars[i]);
		}
		return new String(chars);
	}

	public static String toString(Object value, Object defaultValue) {
		return toString(value, defaultValue, true);
	}

	public static String toString(Object value, Object defaultValue, boolean checkLength) {
		if (value == null) {
			return defaultValue == null ? null : defaultValue.toString();
		}

		String v = (value instanceof String) ? (String) value : value.toString();
		if (checkLength && isEmpty(v)) {
			return defaultValue.toString();
		}
		return v;
	}

	/**
	 * Copy the given Collection into a String array. The Collection must contain
	 * String elements only.
	 * 
	 * @param collection the Collection to copy
	 * @return the String array ({@code null} if the passed-in Collection was
	 *         {@code null})
	 */
	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

	/**
	 * Copy the given Enumeration into a String array. The Enumeration must contain
	 * String elements only.
	 * 
	 * @param enumeration the Enumeration to copy
	 * @return the String array ({@code null} if the passed-in Enumeration was
	 *         {@code null})
	 */
	public static String[] toStringArray(Enumeration<String> enumeration) {
		if (enumeration == null) {
			return null;
		}
		List<String> list = Collections.list(enumeration);
		return list.toArray(new String[list.size()]);
	}

	public static String toUpperCase(String text, int begin, int end) {
		char[] chars = text.toCharArray();
		for (int i = begin; i < end; i++) {
			chars[i] = Character.toUpperCase(chars[i]);
		}
		return new String(chars);
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

	/**
	 * Trim <i>all</i> whitespace from the given String: leading, trailing, and
	 * inbetween characters.
	 * 
	 * @param charSequence the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static CharSequence trimAllWhitespace(CharSequence charSequence) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return sb;
	}

	public static String trimAllWhitespace(String charSequence) {
		if (charSequence == null) {
			return charSequence;
		}

		return trimAllWhitespace((CharSequence) charSequence).toString();
	}

	/**
	 * Trim the elements of the given String array, calling {@code String.trim()} on
	 * each of them.
	 * 
	 * @param array the original String array
	 * @return the resulting array (of the same size) with trimmed elements
	 */
	public static String[] trimArrayElements(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return new String[0];
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			result[i] = (element != null ? element.trim() : null);
		}
		return result;
	}

	/**
	 * Trim all occurences of the supplied leading character from the given String.
	 * 
	 * @param charSequence     the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static CharSequence trimLeadingCharacter(CharSequence charSequence, char leadingCharacter) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb;
	}

	public static String trimLeadingCharacter(String charSequence, char leadingCharacter) {
		if (charSequence == null) {
			return charSequence;
		}

		return trimLeadingCharacter((CharSequence) charSequence, leadingCharacter).toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 * 
	 * @param charSequence the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static CharSequence trimLeadingWhitespace(CharSequence charSequence) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb;
	}

	public static String trimLeadingWhitespace(String charSequence) {
		if (charSequence == null) {
			return charSequence;
		}

		return trimLeadingWhitespace((CharSequence) charSequence).toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given String.
	 * 
	 * @param charSequence      the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static CharSequence trimTrailingCharacter(CharSequence charSequence, char trailingCharacter) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public static String trimTrailingCharacter(String charSequence, char trailingCharacter) {
		if (charSequence == null) {
			return charSequence;
		}
		return trimTrailingCharacter((CharSequence) charSequence, trailingCharacter).toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 * 
	 * @param charSequence the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static CharSequence trimTrailingWhitespace(CharSequence charSequence) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public static String trimTrailingWhitespace(String charSequence) {
		if (charSequence == null) {
			return charSequence;
		}

		return trimTrailingWhitespace((CharSequence) charSequence).toString();
	}

	/**
	 * Trim leading and trailing whitespace from the given String.
	 * 
	 * @param charSequence the String to check
	 * @return the trimmed {@link CharSequence}
	 * @see java.lang.Character#isWhitespace
	 */
	public static CharSequence trimWhitespace(CharSequence charSequence) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		StringBuilder sb = new StringBuilder(charSequence);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public static String trimWhitespace(String charSequence) {
		if (charSequence == null) {
			return charSequence;
		}
		return trimWhitespace((CharSequence) charSequence).toString();
	}

	/**
	 * Uncapitalize a {@code String}, changing the first letter to lower case as per
	 * {@link Character#toLowerCase(char)}. No other letters are changed.
	 * 
	 * @param str the {@code String} to uncapitalize
	 * @return the uncapitalized {@code String}
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * Unqualify a string qualified by a '.' dot character. For example,
	 * "this.name.is.qualified", returns "qualified".
	 * 
	 * @param qualifiedName the qualified name
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * Unqualify a string qualified by a separator character. For example,
	 * "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * 
	 * @param qualifiedName the qualified name
	 * @param separator     the separator
	 */
	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * 验证包名是否合法
	 * 
	 * @param packageName
	 * @return
	 */
	public static boolean verifyPackageName(CharSequence packageName) {
		if (isEmpty(packageName)) {
			return false;
		}

		return split(packageName, ".")
				.allMatch((e) -> e.getSource().length() > 0 && Character.isAlphabetic(e.charAt(0)));
	}

	private StringUtils() {
	}
}
