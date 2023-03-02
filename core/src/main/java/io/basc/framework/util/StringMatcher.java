package io.basc.framework.util;

/**
 * 字符串匹配
 * 
 * @author wcnnkh
 *
 */
public interface StringMatcher extends Matcher<String> {

	default StringMatcher split(String split) {
		return new SplitStringMatcher(this, split);
	}
}
