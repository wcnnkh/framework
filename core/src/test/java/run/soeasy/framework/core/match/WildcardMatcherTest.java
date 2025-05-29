package run.soeasy.framework.core.match;

import org.junit.Test;

public class WildcardMatcherTest {
	@Test
	public void test() {
		StringMatcher matcher = WildcardMatcher.INSTANCE;

		// 测试 ?（匹配单个字符）
		System.out.println(matcher.match("h?llo", "hello")); // true
		System.out.println(matcher.match("h?llo", "hallo")); // true
		System.out.println(matcher.match("h?llo", "hhllo")); // false

		// 测试 *（匹配0个或多个字符）
		System.out.println(matcher.match("h*o", "hello")); // true
		System.out.println(matcher.match("h*o", "ho")); // true（*匹配0个字符）
		System.out.println(matcher.match("h*o", "hox")); // false

		// 测试组合
		System.out.println(matcher.match("h*?o", "hello")); // true
		System.out.println(matcher.match("h*?o", "heo")); // true（*匹配0个，?匹配e）

		// 测试 extractWithinPattern
		System.out.println(matcher.extractWithinPattern("h*llo", "helloworld")); // world
		System.out.println(matcher.extractWithinPattern("h?*o", "hello")); // ""
	}
}
