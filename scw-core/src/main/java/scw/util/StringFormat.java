package scw.util;

import scw.core.Assert;
import scw.core.utils.StringUtils;

public class StringFormat{
	private final char[] prefix;
	private final char[] suffix;
	private final PlaceholderResolver placeholderResolver;

	protected StringFormat(String prefix, String suffix, PlaceholderResolver placeholderResolver) {
		Assert.hasLength(prefix);
		Assert.hasLength(suffix);

		this.prefix = prefix.toCharArray();
		this.suffix = suffix.toCharArray();
		this.placeholderResolver = placeholderResolver;
	}

	public char[] getPrefix() {
		return prefix;
	}

	public char[] getSuffix() {
		return suffix;
	}

	public final String format(final String text) {
		if (StringUtils.isEmpty(text)) {
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
						value = placeholderResolver.resolvePlaceholder(new String(chars, tempBegin
								+ prefix.length, begin - tempBegin
								- prefix.length));
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
	
	public static String format(String text, String prefix, String suffix, PlaceholderResolver placeholderResolver){
		return new StringFormat(prefix, suffix, placeholderResolver).format(text);
	}
	
	public static String format(String text, PlaceholderResolver placeholderResolver) {
		String textToUse = format(text, "${", "}", placeholderResolver);
		return format(textToUse, "{", "}", placeholderResolver);
	}
}
