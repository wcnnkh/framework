package shuchaowen.common.utils;

import shuchaowen.core.util.StringUtils;

public abstract class StringFormat{
	private char[] prefix;
	private char[] suffix;

	public StringFormat(String prefix, String suffix) {
		if (StringUtils.isNull(prefix, suffix)) {
			throw new NullPointerException();
		}

		this.prefix = prefix.toCharArray();
		this.suffix = suffix.toCharArray();
	}

	public final String format(final String text) {
		if(StringUtils.isNull(text)){
			return text;
		}
		
		char[] chars = text.toCharArray();
		StringBuilder sb = new StringBuilder(chars.length * 2);
		int begin = 0;
		while (begin < chars.length) {
			if (prefixEq(chars, begin)) {
				int tempBegin = begin;
				begin += prefix.length;
				String value = null;
				while (begin < chars.length) {
					if (suffixEq(chars, begin)) {
						value = getValue(new String(chars, tempBegin + prefix.length, begin - tempBegin - 1));
						begin += suffix.length;
						break;
					} else {
						begin++;
					}
				}

				if (value == null) {
					sb.append(chars, tempBegin, begin - tempBegin);
				} else {
					sb.append(value);
				}
			} else {
				sb.append(chars[begin]);
				begin++;
			}
		}
		return sb.toString();
	}

	private boolean prefixEq(char[] chars, int begin) {
		int v = 0;
		for (int i = begin; i < begin + prefix.length; i++, v++) {
			if (i >= chars.length || chars[i] != prefix[v]) {
				return false;
			}
		}
		return true;
	}

	private boolean suffixEq(char[] chars, int begin) {
		int v = 0;
		for (int i = begin; i < begin + suffix.length; i++, v++) {
			if (i >= chars.length || chars[i] != suffix[v]) {
				return false;
			}
		}
		return true;
	}
	
	protected abstract String getValue(final String key);
}
