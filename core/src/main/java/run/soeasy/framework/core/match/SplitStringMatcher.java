package run.soeasy.framework.core.match;

import run.soeasy.framework.core.StringUtils;

public class SplitStringMatcher implements StringMatcher {
	private final StringMatcher stringMatcher;
	private final String split;

	public SplitStringMatcher(StringMatcher stringMatcher, String split) {
		this.stringMatcher = stringMatcher;
		this.split = split;
	}

	public final StringMatcher getStringMatcher() {
		return stringMatcher;
	}

	public final String getSplit() {
		return split;
	}

	public String[] splitText(String text) {
		return StringUtils.splitToArray(text, split);
	}

	@Override
	public boolean isPattern(String text) {
		if (text == null) {
			return false;
		}

		for (String str : splitText(text)) {
			if (!stringMatcher.isPattern(str)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean match(String pattern, String text) {
		if (pattern == null || text == null) {
			return false;
		}

		String[] patternArray = splitText(pattern);
		String[] textArray = splitText(text);
		if (patternArray.length != textArray.length) {
			return false;
		}

		for (int i = 0; i < patternArray.length; i++) {
			if (!StringMatchers.match(stringMatcher, patternArray[i], textArray[i])) {
				return false;
			}
		}
		return true;
	}

}
