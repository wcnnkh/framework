package run.soeasy.framework.core.match;

/**
 * 标准通配符匹配器，支持： - ?：匹配任意单个字符 - *：匹配任意数量（包括0个）的任意字符序列
 */
class WildcardMatcher implements StringMatcher {
	static final WildcardMatcher INSTANCE = new WildcardMatcher();
	private static final char ANY_ONE = '?';
	private static final char ANY_ZERO_OR_MORE = '*';

	private WildcardMatcher() {
	}

	@Override
	public boolean isPattern(String text) {
		return text != null && (text.indexOf(ANY_ONE) >= 0 || text.indexOf(ANY_ZERO_OR_MORE) >= 0);
	}

	@Override
	public boolean match(String pattern, String text) {
		if (pattern == null || text == null) {
			return false;
		}
		return matchRecursive(pattern, text, 0, 0);
	}

	private boolean matchRecursive(String pattern, String text, int pIdx, int tIdx) {
		// 模式和文本都到达末尾，匹配成功
		if (pIdx == pattern.length() && tIdx == text.length()) {
			return true;
		}

		// 模式到达末尾但文本未结束，匹配失败
		if (pIdx == pattern.length()) {
			return false;
		}

		char currentChar = pattern.charAt(pIdx);

		// 处理 *：匹配0个或多个任意字符
		if (currentChar == ANY_ZERO_OR_MORE) {
			// 尝试匹配0次、1次、2次...直到文本结束
			for (int i = tIdx; i <= text.length(); i++) {
				if (matchRecursive(pattern, text, pIdx + 1, i)) {
					return true;
				}
			}
			return false;
		}

		// 处理 ?：匹配单个任意字符
		if (currentChar == ANY_ONE) {
			// 文本已结束，无法匹配单个字符
			if (tIdx >= text.length()) {
				return false;
			}
			return matchRecursive(pattern, text, pIdx + 1, tIdx + 1);
		}

		// 处理普通字符：必须精确匹配
		if (tIdx >= text.length() || currentChar != text.charAt(tIdx)) {
			return false;
		}

		// 继续匹配下一个字符
		return matchRecursive(pattern, text, pIdx + 1, tIdx + 1);
	}

	@Override
	public String extractWithinPattern(String pattern, String text) {
		if (!match(pattern, text)) {
			return text;
		}

		// 计算匹配的前缀长度（尽可能少地匹配*）
		int prefixLength = calculateMatchedPrefixLength(pattern, text);
		return text.substring(prefixLength);
	}

	private int calculateMatchedPrefixLength(String pattern, String text) {
		int pIdx = 0, tIdx = 0;
		int lastStarIdx = -1;
		int lastStarMatch = 0;

		while (tIdx < text.length()) {
			if (pIdx < pattern.length()) {
				char currentChar = pattern.charAt(pIdx);

				if (currentChar == ANY_ZERO_OR_MORE) {
					// 记录*的位置和当前文本位置
					lastStarIdx = pIdx;
					lastStarMatch = tIdx;
					pIdx++;
				} else if (currentChar == ANY_ONE || currentChar == text.charAt(tIdx)) {
					// 匹配?或普通字符
					pIdx++;
					tIdx++;
				} else if (lastStarIdx != -1) {
					// 回溯到上一个*，让它多匹配一个字符
					pIdx = lastStarIdx + 1;
					lastStarMatch++;
					tIdx = lastStarMatch;
				} else {
					break;
				}
			} else if (lastStarIdx != -1) {
				// 模式已结束，但还有*未处理
				pIdx = lastStarIdx + 1;
				lastStarMatch++;
				tIdx = lastStarMatch;
			} else {
				break;
			}
		}

		// 处理模式末尾的*
		while (pIdx < pattern.length() && pattern.charAt(pIdx) == ANY_ZERO_OR_MORE) {
			pIdx++;
		}

		return (pIdx == pattern.length()) ? tIdx : 0;
	}

	@Override
	public int compare(String p1, String p2) {
		// 优先比较非通配符字符长度（长的优先）
		int nonWildcardLen1 = countNonWildcardChars(p1);
		int nonWildcardLen2 = countNonWildcardChars(p2);

		int lenDiff = nonWildcardLen2 - nonWildcardLen1;
		if (lenDiff != 0) {
			return lenDiff;
		}

		// 非通配符长度相同，比较总长度
		return p2.length() - p1.length();
	}

	private int countNonWildcardChars(String pattern) {
		int count = 0;
		for (char c : pattern.toCharArray()) {
			if (c != ANY_ONE && c != ANY_ZERO_OR_MORE) {
				count++;
			}
		}
		return count;
	}
}