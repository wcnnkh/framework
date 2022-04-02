package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;

public class SimplePlaceholderReplaer implements PlaceholderReplacer {

	public static final PlaceholderReplacer STRICT_REPLACER = new SimplePlaceholderReplaer(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, false);

	public static final PlaceholderReplacer NON_STRICT_REPLACER = new SimplePlaceholderReplaer(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, true);

	private final char[] prefix;
	private final char[] suffix;
	private final boolean ignoreUnresolvablePlaceholders;

	public SimplePlaceholderReplaer(String prefix, String suffix, boolean ignoreUnresolvablePlaceholders) {
		Assert.hasLength(prefix);
		Assert.hasLength(suffix);

		this.prefix = prefix.toCharArray();
		this.suffix = suffix.toCharArray();
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	public char[] getPrefix() {
		return prefix;
	}

	public char[] getSuffix() {
		return suffix;
	}

	public String replacePlaceholders(String text, PlaceholderResolver placeholderResolver,
			boolean ignoreUnresolvablePlaceholders) {
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
						String placeholder = new String(chars, tempBegin + prefix.length,
								begin - tempBegin - prefix.length);
						value = placeholderResolver.resolvePlaceholder(placeholder);
						if (value == null && !ignoreUnresolvablePlaceholders) {
							throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'"
									+ " in string value \"" + text + "\"");
						}
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

	@Override
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		return replacePlaceholders(value, placeholderResolver, this.ignoreUnresolvablePlaceholders);
	}

	@Override
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver)
			throws IllegalArgumentException {
		return replacePlaceholders(value, placeholderResolver, false);
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
}
