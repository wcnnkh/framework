package run.soeasy.framework.core.match;

import org.junit.Test;

public class StartsWitchMatcherTest {
	@Test
	public void test() {
		StringMatcher matcher = PrefixMatcher.DEFAULT;

		// 测试 match
		System.out.println(matcher.match("hello", "hello-world")); // true
		System.out.println(matcher.match("world", "hello-world")); // false

		// 测试 extractWithinPattern
		System.out.println(matcher.extractWithinPattern("hello", "hello-world")); // -world
		System.out.println(matcher.extractWithinPattern("user-", "user-123")); // 123
		System.out.println(matcher.extractWithinPattern("abc", "abcd")); // d
	}
}
