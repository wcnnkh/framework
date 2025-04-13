package run.soeasy.framework.core.match;

import run.soeasy.framework.core.strings.StringUtils;

/**
 * 常用的匹配方式
 * 
 * @author wcnnkh
 *
 */
public enum StringMatchers implements StringMatcher {
	/**
	 * 不进行任何匹配
	 */
	NOTHING,
	/**
	 * 前缀匹配
	 */
	PREFIX,
	/**
	 * 判断字符串是否与通配符匹配 只能存在通配符*和? ?代表1个 *代表0个或多个
	 * !开头代表非(只支持开头使用!)
	 * 
	 * @see SimpleStringMatcher
	 */
	SIMPLE,;

	private static final SimpleStringMatcher simpleStringMatcher = new SimpleStringMatcher();

	@Override
	public boolean isPattern(String text) {
		if (text == null) {
			return false;
		}

		switch (this) {
		case PREFIX:
			return true;
		case SIMPLE:
			return simpleStringMatcher.isPattern(text);
		default:
			return false;
		}
	}

	@Override
	public boolean match(String pattern, String text) {
		switch (this) {
		case PREFIX:
			return text.startsWith(pattern);
		case SIMPLE:
			return simpleStringMatcher.match(pattern, text);
		default:
			return false;
		}
	}

	public static boolean match(StringMatcher stringMatcher, String pattern, String text) {
		if (pattern != null && stringMatcher.isPattern(pattern)) {
			return stringMatcher.match(pattern, text);
		} else {
			return StringUtils.equals(pattern, text);
		}
	}

	public static boolean matchAny(String pattern, String text) {
		for (StringMatchers matchers : values()) {
			if (matchers.match(pattern, text)) {
				return true;
			}
		}
		return false;
	}
}
