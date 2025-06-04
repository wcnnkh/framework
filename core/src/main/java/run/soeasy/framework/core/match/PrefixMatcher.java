package run.soeasy.framework.core.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.StringUtils;

/**
 * 固定前缀匹配器，严格基于 text.startsWith(pattern) 实现。 模式本身就是完整前缀，不支持任何通配符或特殊字符。
 */
@RequiredArgsConstructor
@Getter
class PrefixMatcher implements StringMatcher {
	static final PrefixMatcher DEFAULT = new PrefixMatcher(false);
	/**
	 * 忽略大小写
	 */
	static final PrefixMatcher IGNORE_CASE = new PrefixMatcher(true);
	private final boolean ignoreCase;

	@Override
	public boolean isPattern(String text) {
		// 固定前缀匹配器不支持模式，所有文本都被视为普通前缀
		return false;
	}

	@Override
	public boolean match(String pattern, String text) {
		if (pattern == null || text == null) {
			return false;
		}
		return StringUtils.startsWith(text, pattern, ignoreCase);
	}

	@Override
	public String extractWithinPattern(String pattern, String text) {
		if (!match(pattern, text)) {
			return text;
		}
		// 返回前缀后的剩余部分
		return text.substring(pattern.length());
	}

	@Override
	public int compare(String p1, String p2) {
		// 按前缀长度降序排序（长前缀优先）
		int lenDiff = p2.length() - p1.length();
		if (lenDiff != 0) {
			return lenDiff;
		}
		return p1.compareTo(p2);
	}
}