package scw.core.utils;

import java.util.LinkedList;

public final class StringSplitUtils {
	private StringSplitUtils(){
	}
	
	public static String[] split(String str, char... filters) {
		if (str == null || str.length() == 0) {
			return null;
		}

		return split(str, true, filters);
	}

	public static String[] split(String str, String... filters) {
		if (str == null || str.length() == 0) {
			return null;
		}

		return split(str, true, filters);
	}

	public static String[] split(String str, boolean ignoreNull, char... filters) {
		if (str == null) {
			return null;
		}

		LinkedList<String> list = new LinkedList<String>();
		int begin = 0;
		for (int i = 0, size = str.length(); i < size; i++) {
			char c = str.charAt(i);
			boolean find = false;
			for (char s : filters) {
				if (c == s) {
					find = true;
					break;
				}
			}

			if (find) {
				if (ignoreNull && i == begin) {
					continue;
				}

				list.add(str.substring(begin, i));
				begin = i + 1;
			}
		}

		if (begin == 0) {
			return new String[] { str };
		}
		return list.toArray(new String[list.size()]);
	}

	public static String[] split(String str, boolean ignoreNull, String... filters) {
		if (str == null) {
			return null;
		}

		int begin = 0;
		int index = -1;
		String v = null;
		for (String f : filters) {
			index = str.indexOf(f, begin);
			if (index != -1) {
				v = f;
				break;
			}
		}

		if (index == -1) {
			return new String[] { str };
		}

		LinkedList<String> list = new LinkedList<String>();
		while (index != -1 && v != null) {
			list.add(str.substring(begin, index));
			begin = index + v.length();

			for (String f : filters) {
				index = str.indexOf(f, begin);
				if (index != -1) {
					v = f;
					break;
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
