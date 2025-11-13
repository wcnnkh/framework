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

/**
 * 字符串工具类 提供字符串操作的各种实用方法，包括查找、替换、分割、比较等功能
 * 
 * @author soeasy.run
 */
@UtilityClass
public class StringUtils {
	public static final String[] EMPTY_ARRAY = new String[0];

	/**
	 * 计算目标字符序列在源字符序列中的出现次数
	 * 
	 * @param charSequence 源字符序列
	 * @param target       目标字符序列
	 * @return 出现次数
	 */
	public static int count(CharSequence charSequence, CharSequence target) {
		return count(charSequence, 0, target);
	}

	/**
	 * 从指定位置开始计算目标字符序列在源字符序列中的出现次数
	 * 
	 * @param charSequence 源字符序列
	 * @param beginIndex   开始位置
	 * @param target       目标字符序列
	 * @return 出现次数
	 */
	public static int count(CharSequence charSequence, int beginIndex, CharSequence target) {
		if (charSequence == null) {
			return 0;
		}

		return count(charSequence, beginIndex, charSequence.length(), target);
	}

	/**
	 * 从指定范围计算目标字符序列在源字符序列中的出现次数
	 * 
	 * @param charSequence 源字符序列
	 * @param beginIndex   开始位置
	 * @param endIndex     结束位置
	 * @param target       目标字符序列
	 * @return 出现次数
	 */
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
	 * 不区分大小写检查字符串是否以指定后缀结尾
	 * 
	 * @param str    源字符串
	 * @param suffix 后缀
	 * @return 是否以指定后缀结尾
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

	/**
	 * 比较字符序列的子区域是否相等
	 * 
	 * @param cs         源字符序列
	 * @param ignoreCase 是否忽略大小写
	 * @param thisStart  源序列起始位置
	 * @param substring  目标子序列
	 * @param start      目标子序列起始位置
	 * @param length     比较长度
	 * @return 是否相等
	 */
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

	/**
	 * 比较两个字符序列是否相等
	 * 
	 * @param a 源字符序列
	 * @param b 目标字符序列
	 * @return 是否相等
	 */
	public static boolean equals(CharSequence a, CharSequence b) {
		return equals(a, b, false);
	}

	/**
	 * 比较两个字符序列是否相等（可忽略大小写）
	 * 
	 * @param a          源字符序列
	 * @param b          目标字符序列
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否相等
	 */
	public static boolean equals(CharSequence a, CharSequence b, boolean ignoreCase) {
		return equals(a, ignoreCase, 0, b, 0, a.length());
	}

	/**
	 * 检查字符序列是否包含文本（非空且非全空白）
	 * 
	 * @param value 字符序列
	 * @return 是否包含有效文本
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

	/**
	 * 检查字符序列的指定区域是否包含文本
	 * 
	 * @param value     字符序列
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 是否包含有效文本
	 */
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
	 * 查找字符数组中目标数组的首次出现位置
	 * 
	 * @param source 源字符数组
	 * @param target 目标字符数组
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(char[] source, char[] target) {
		return indexOf(source, target, 0);
	}

	/**
	 * 查找字符数组中指定前缀和后缀之间的区域
	 * 
	 * @param source    源字符数组
	 * @param prefix    前缀数组
	 * @param suffix    后缀数组
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 包含前缀和后缀的区域范围，未找到返回null
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
		while (true) { // 重复查找是否存在嵌套,直到找到最外层的{suffix}
			int nestingLevel = 0; // 嵌套了多少层
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
				if (tempEnd == -1) { // 两边的符号嵌套层级不一至
					return null;
				}
			}
		}
		return Range.closed(begin, tempEnd);
	}

	/**
	 * 从指定位置开始查找字符数组中目标数组的首次出现位置
	 * 
	 * @param source    源字符数组
	 * @param target    目标字符数组
	 * @param fromIndex 起始位置
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(char[] source, char[] target, int fromIndex) {
		return indexOf(source, target, fromIndex, source.length);
	}

	/**
	 * 在字符数组的指定范围内查找目标数组的首次出现位置
	 * 
	 * @param source    源字符数组
	 * @param target    目标字符数组
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(char[] source, char[] target, int fromIndex, int endIndex) {
		int index = indexOf(source, fromIndex, endIndex - fromIndex, target, 0, target.length, 0);
		return index == -1 ? -1 : index + fromIndex;
	}

	/**
	 * 在字符数组的指定子区域中查找目标数组的首次出现位置
	 * 
	 * @param source       源字符数组
	 * @param sourceOffset 源数组偏移量
	 * @param sourceCount  源数组计数
	 * @param target       目标字符数组
	 * @param targetOffset 目标数组偏移量
	 * @param targetCount  目标数组计数
	 * @param fromIndex    起始位置
	 * @return 首次出现位置，未找到返回-1
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

	/**
	 * 查找字符序列中目标序列的首次出现位置
	 * 
	 * @param source 源字符序列
	 * @param target 目标字符序列
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(CharSequence source, CharSequence target) {
		return indexOf(source, target, 0);
	}

	/**
	 * 查找字符序列中指定前缀和后缀之间的区域
	 * 
	 * @param text   源字符序列
	 * @param prefix 前缀序列
	 * @param suffix 后缀序列
	 * @return 包含前缀和后缀的区域范围，未找到返回null
	 */
	public static Range<Integer> indexOf(CharSequence text, CharSequence prefix, CharSequence suffix) {
		return indexOf(text, prefix, suffix, 0);
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	// ---------------------------------------------------------------------

	/**
	 * 从指定位置开始查找字符序列中指定前缀和后缀之间的区域
	 * 
	 * @param source    源字符序列
	 * @param prefix    前缀序列
	 * @param suffix    后缀序列
	 * @param fromIndex 起始位置
	 * @return 包含前缀和后缀的区域范围，未找到返回null
	 */
	public static Range<Integer> indexOf(CharSequence source, CharSequence prefix, CharSequence suffix, int fromIndex) {
		if (source == null) {
			return null;
		}

		return indexOf(source, prefix, suffix, fromIndex, source.length());
	}

	/**
	 * 在字符序列的指定范围内查找指定前缀和后缀之间的区域
	 * 
	 * @param source    源字符序列
	 * @param prefix    前缀序列
	 * @param suffix    后缀序列
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 包含前缀和后缀的区域范围，未找到返回null
	 */
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
		while (true) { // 重复查找是否存在嵌套,直到找到最外层的{suffix}
			int nestingLevel = 0; // 嵌套了多少层
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
				if (tempEnd == -1) { // 两边的符号嵌套层级不一至
					return null;
				}
			}
		}
		return Range.closed(begin, tempEnd);
	}

	/**
	 * 从指定位置开始查找字符序列中目标序列的首次出现位置
	 * 
	 * @param source    源字符序列
	 * @param target    目标字符序列
	 * @param fromIndex 起始位置
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(CharSequence source, CharSequence target, int fromIndex) {
		if (source == null) {
			return -1;
		}
		return indexOf(source, target, fromIndex, source.length());
	}

	/**
	 * 在字符序列的指定范围内查找目标序列的首次出现位置
	 * 
	 * @param source    源字符序列
	 * @param target    目标字符序列
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 首次出现位置，未找到返回-1
	 */
	public static int indexOf(CharSequence source, CharSequence target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}
		int index = indexOf(source, fromIndex, endIndex - fromIndex, target, 0, target.length(), 0);
		return index == -1 ? -1 : index + fromIndex;
	}

	/**
	 * 在字符序列的指定子区域中查找目标序列的首次出现位置
	 * 
	 * @param source       源字符序列
	 * @param sourceOffset 源序列偏移量
	 * @param sourceCount  源序列计数
	 * @param target       目标字符序列
	 * @param targetOffset 目标序列偏移量
	 * @param targetCount  目标序列计数
	 * @param fromIndex    起始位置
	 * @return 首次出现位置，未找到返回-1
	 */
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

	/**
	 * 检查字符序列是否为空
	 * 
	 * @param value 字符序列
	 * @return 是否为空
	 */
	public static boolean isEmpty(CharSequence value) {
		return value == null || value.length() == 0;
	}

	/**
	 * 检查字符序列是否非空
	 * 
	 * @param value 字符序列
	 * @return 是否非空
	 */
	public static boolean isNotEmpty(CharSequence value) {
		return !isEmpty(value);
	}

	/**
	 * 查找字符数组中目标数组的最后一次出现位置
	 * 
	 * @param source 源字符数组
	 * @param target 目标字符数组
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(char[] source, char[] target) {
		if (source == null) {
			return -1;
		}

		return lastIndexOf(source, target, source.length);
	}

	/**
	 * 从指定位置开始查找字符数组中目标数组的最后一次出现位置
	 * 
	 * @param source    源字符数组
	 * @param target    目标字符数组
	 * @param fromIndex 起始位置
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(char[] source, char[] target, int fromIndex) {
		return lastIndexOf(source, target, fromIndex, 0);
	}

	/**
	 * 在字符数组的指定范围内查找目标数组的最后一次出现位置
	 * 
	 * @param source    源字符数组
	 * @param target    目标字符数组
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(char[] source, char[] target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}

		int sourceCount = Math.min(fromIndex, source.length) - endIndex;
		int index = lastIndexOf(source, endIndex, sourceCount, target, 0, target.length, sourceCount);
		return index == -1 ? -1 : index + endIndex;
	}

	/**
	 * 在字符数组的指定子区域中查找目标数组的最后一次出现位置
	 * 
	 * @param source       源字符数组
	 * @param sourceOffset 源数组偏移量
	 * @param sourceCount  源数组计数
	 * @param target       目标字符数组
	 * @param targetOffset 目标数组偏移量
	 * @param targetCount  目标数组计数
	 * @param fromIndex    起始位置
	 * @return 最后一次出现位置，未找到返回-1
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

	/**
	 * 查找字符序列中目标序列的最后一次出现位置
	 * 
	 * @param source 源字符序列
	 * @param target 目标字符序列
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(CharSequence source, CharSequence target) {
		if (source == null || target == null) {
			return -1;
		}

		return lastIndexOf(source, target, source.length());
	}

	/**
	 * 从指定位置开始查找字符序列中目标序列的最后一次出现位置
	 * 
	 * @param source    源字符序列
	 * @param target    目标字符序列
	 * @param fromIndex 起始位置
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(CharSequence source, CharSequence target, int fromIndex) {
		return lastIndexOf(source, target, fromIndex, 0);
	}

	/**
	 * 在字符序列的指定范围内查找目标序列的最后一次出现位置
	 * 
	 * @param source    源字符序列
	 * @param target    目标字符序列
	 * @param fromIndex 起始位置
	 * @param endIndex  结束位置
	 * @return 最后一次出现位置，未找到返回-1
	 */
	public static int lastIndexOf(CharSequence source, CharSequence target, int fromIndex, int endIndex) {
		if (source == null || target == null) {
			return -1;
		}

		int sourceCount = Math.min(fromIndex, source.length()) - endIndex;
		int index = lastIndexOf(source, endIndex, sourceCount, target, 0, target.length(), sourceCount);
		return index == -1 ? -1 : index + endIndex;
	}

	/**
	 * 在字符序列的指定子区域中查找目标序列的最后一次出现位置
	 * 
	 * @param source       源字符序列
	 * @param sourceOffset 源序列偏移量
	 * @param sourceCount  源序列计数
	 * @param target       目标字符序列
	 * @param targetOffset 目标序列偏移量
	 * @param targetCount  目标序列计数
	 * @param fromIndex    起始位置
	 * @return 最后一次出现位置，未找到返回-1
	 */
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

	/**
	 * 分割字符序列为元素集合（支持过滤和修剪）
	 * 
	 * @param charSequence      源字符序列
	 * @param trimTokens        是否修剪标记
	 * @param ignoreEmptyTokens 是否忽略空标记
	 * @param filters           分隔符序列
	 * @return 分割后的元素集合
	 */
	public static Elements<CharSequenceTemplate> split(CharSequence charSequence, boolean trimTokens,
			boolean ignoreEmptyTokens, CharSequence... filters) {
		return split(charSequence, filters).map((s) -> trimTokens ? (s == null ? s : s.trim()) : s)
				.filter((s) -> (ignoreEmptyTokens ? StringUtils.isNotEmpty(s) : true));
	}

	/**
	 * 按指定分隔符分割字符序列为元素集合
	 * 
	 * @param charSequence 源字符序列
	 * @param filters      分隔符序列
	 * @return 分割后的元素集合
	 */
	public static Elements<CharSequenceTemplate> split(CharSequence charSequence, CharSequence... filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), Arrays.asList(filters));
	}

	/**
	 * 按指定分隔符集合分割字符序列为元素集合
	 * 
	 * @param charSequence 源字符序列
	 * @param filters      分隔符集合
	 * @return 分割后的元素集合
	 */
	public static Elements<CharSequenceTemplate> split(CharSequence charSequence,
			Collection<? extends CharSequence> filters) {
		if (charSequence == null) {
			return Elements.empty();
		}
		return split(charSequence, 0, charSequence.length(), filters);
	}

	/**
	 * 在指定范围内按分隔符集合分割字符序列为元素集合
	 * 
	 * @param charSequence 源字符序列
	 * @param beginIndex   起始位置
	 * @param endIndex     结束位置
	 * @param filters      分隔符集合
	 * @return 分割后的元素集合
	 */
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

	/**
	 * 分割字符序列为字符串数组（支持过滤和修剪）
	 * 
	 * @param charSequence      源字符序列
	 * @param trimTokens        是否修剪标记
	 * @param ignoreEmptyTokens 是否忽略空标记
	 * @param filters           分隔符序列
	 * @return 分割后的字符串数组
	 */
	public static String[] splitToArray(CharSequence charSequence, boolean trimTokens, boolean ignoreEmptyTokens,
			CharSequence... filters) {
		return split(charSequence, trimTokens, ignoreEmptyTokens, filters).map((s) -> s == null ? null : s.toString())
				.toArray(String[]::new);
	}

	/**
	 * 按指定分隔符分割字符序列为字符串数组
	 * 
	 * @param charSequence 源字符序列
	 * @param filters      分隔符序列
	 * @return 分割后的字符串数组
	 */
	public static String[] splitToArray(CharSequence charSequence, CharSequence... filters) {
		return splitToArray(charSequence, true, true, filters);
	}

	/**
	 * 检查字符串是否以指定前缀开始（支持忽略大小写）
	 * 
	 * @param text       源字符串
	 * @param prefix     前缀
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否以指定前缀开始
	 */
	public static boolean startsWith(String text, String prefix, boolean ignoreCase) {
		return startsWith(text, prefix, 0, ignoreCase);
	}

	/**
	 * 检查字符串的指定区域是否以指定前缀开始（支持忽略大小写）
	 * 
	 * @param text       源字符串
	 * @param prefix     前缀
	 * @param toOffset   结束偏移量
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否以指定前缀开始
	 */
	public static boolean startsWith(String text, String prefix, int toOffset, boolean ignoreCase) {
		if (ignoreCase) {
			int to = toOffset;
			int po = 0;
			int pc = prefix.length();
			// Note: toffset might be near -1>>>1.
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
	 * 不区分大小写检查字符串是否以指定前缀开始
	 * 
	 * @param str    源字符串
	 * @param prefix 前缀
	 * @return 是否以指定前缀开始
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
	 * 将字符串按指定分隔符分词为元素集合
	 * 
	 * @param text       源字符串
	 * @param delimiters 分隔符
	 * @return 分词后的元素集合
	 */
	public static Elements<String> tokenize(String text, String delimiters) {
		if (StringUtils.isEmpty(text)) {
			return Elements.empty();
		}

		return tokenize(new StringTokenizer(text, delimiters));
	}

	/**
	 * 将字符串按指定分隔符分词为元素集合（支持修剪和过滤空标记）
	 * 
	 * @param str               源字符串
	 * @param delimiters        分隔符
	 * @param trimTokens        是否修剪标记
	 * @param ignoreEmptyTokens 是否忽略空标记
	 * @return 分词后的元素集合
	 */
	public static Elements<String> tokenize(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {
		return tokenize(str, delimiters).map((s) -> trimTokens ? (s == null ? s : s.trim()) : s)
				.filter((s) -> (ignoreEmptyTokens ? StringUtils.isNotEmpty(s) : true));
	}

	/**
	 * 将字符串分词器转换为元素集合
	 * 
	 * @param tokenizer 字符串分词器
	 * @return 分词后的元素集合
	 */
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
	 * @param text  源字符串
	 * @param chars 要转义的字符
	 * @return 转义后的字符串
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