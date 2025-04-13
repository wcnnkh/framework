package run.soeasy.framework.core.match;

import run.soeasy.framework.core.strings.StringUtils;

/**
 * 判断字符串是否与通配符匹配 只能存在通配符*和? ?代表1个 *代表0个或多个
 * !开头代表非(只支持开头使用!)
 * 
 * @author wcnnkh
 *
 */
public class SimpleStringMatcher implements StringMatcher {

	public SimpleStringMatcher() {
	}

	private boolean testInternal(String text, String match) {
		if (text == null) {
			return "*".equals(match);
		}

		if ("*".equals(match)) {
			return true;
		}

		if (match.indexOf("*") == -1) {
			if (match.indexOf("?") == -1) {
				return text.equals(match);
			} else {
				return test(text, match, '?', false);
			}
		}

		String[] arr = StringUtils.splitToArray(match, false, false, "*");
		if (!match.startsWith("*")) {
			if (!text.startsWith(arr[0])) {
				return false;
			}
		}

		if (!match.endsWith("*")) {
			if (!text.endsWith(arr[arr.length - 1])) {
				return false;
			}
		}

		int begin = 0;
		int len = text.length();
		for (String v : arr) {
			int vLen = v.length();
			if (len < vLen) {
				return false;
			}

			boolean b = false;
			int a = begin;
			for (; a < len; a++) {
				int end = a + vLen;
				if (end > text.length()) {
					return false;
				}

				String c = text.substring(a, end);
				if (test(c, v, '?', false)) {
					b = true;
					break;
				}
			}

			if (!b) {
				return false;
			}

			begin = a + vLen;
		}

		return true;
	}

	private boolean test(String text, String match, char matchChar, boolean multiple) {
		if (match.indexOf(matchChar) == -1) {
			return text.equals(match);
		}

		int size = match.length();
		if (multiple) {
			int index = 0;
			int findIndex = 0;
			for (int i = 0; i < size; i++) {
				char c = match.charAt(i);
				if (c != matchChar) {
					continue;
				}

				String v = match.substring(index, i);
				index = i;
				if (v.length() == 0) {
					continue;
				}

				int tempIndex = text.indexOf(v, findIndex);
				if (tempIndex == -1) {
					return false;
				}

				findIndex = tempIndex + v.length();
			}
			return true;
		} else {
			if (text.length() != size) {
				return false;
			}

			for (int i = 0; i < size; i++) {
				if (match.charAt(i) == matchChar) {
					continue;
				}

				if (match.charAt(i) != text.charAt(i)) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean isPattern(String text) {
		return !StringUtils.isEmpty(text)
				&& (text.indexOf("!") != -1 || text.indexOf("?") != -1 || text.indexOf("*") != -1);
	}

	public boolean match(String pattern, String text) {
		if (StringUtils.isEmpty(pattern)) {
			return false;
		}

		if (pattern.startsWith("!")) {
			// 是否进行'非'处理
			return !testInternal(text, pattern.substring(1));
		}
		return testInternal(text, pattern);
	}
}
