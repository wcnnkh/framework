package scw.core.utils;

import java.util.LinkedList;

public final class StringSplitUtils {
	private StringSplitUtils() {
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
		int size = str.length();
		for (int i = 0; i < size; i++) {
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
		} else {
			if (begin != size - 1) {
				list.add(str.substring(begin));
			}
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

		if (begin != str.length() - 1) {
			list.add(str.substring(begin));
		}

		return list.toArray(new String[list.size()]);
	}

	public static int[] splitIntArray(String str, String... filter) {
		String[] arr = split(str, filter);
		if (arr == null) {
			return null;
		}

		int[] dataArr = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			dataArr[i] = Integer.parseInt(arr[i]);
		}
		return dataArr;
	}

	public static long[] splitLongArray(String str, String... filter) {
		String[] arr = split(str, filter);
		if (arr == null) {
			return null;
		}

		return StringParseUtils.parseLongArray(arr);
	}
}
