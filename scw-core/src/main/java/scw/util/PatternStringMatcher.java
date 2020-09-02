package scw.util;

import scw.core.utils.StringUtils;

//TODO 未完成
@Deprecated
public final class PatternStringMatcher implements StringMatcher {
	private final String pattern;
	private final boolean multiple;

	/**
	 * @param pattern
	 *            匹配字符模版
	 * @param multiple
	 *            是否可以多个匹配
	 */
	public PatternStringMatcher(String pattern, boolean multiple) {
		this.pattern = pattern;
		this.multiple = multiple;
	}

	public String getPattern() {
		return pattern;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public boolean isPattern(String text) {
		return StringUtils.isNotEmpty(text) && text.indexOf(pattern) != -1;
	}

	public boolean match(String pattern, String text) {
		if (StringUtils.isEmpty(pattern) && StringUtils.isEmpty(text)) {
			return true;
		}

		if (StringUtils.isEmpty(text)) {
			return this.pattern.equals(pattern);
		}

		if (StringUtils.isEmpty(pattern)) {
			return text.equals(this.pattern);
		}

		if (!isPattern(text)) {
			return pattern.equals(text);
		}
		return false;
	}
}
