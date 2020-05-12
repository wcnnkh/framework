package scw.core;

import java.util.Map;

import scw.core.utils.StringUtils;

public abstract class StringFormat {
	private final char[] prefix;
	private final char[] suffix;

	public StringFormat(String prefix, String suffix) {
		Assert.hasLength(prefix);
		Assert.hasLength(suffix);

		this.prefix = prefix.toCharArray();
		this.suffix = suffix.toCharArray();
	}

	public char[] getPrefix() {
		return prefix;
	}

	public char[] getSuffix() {
		return suffix;
	}

	public final String format(final String text) {
		if (StringUtils.isNull(text)) {
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
						value = getValue(new String(chars, tempBegin
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

	public static String format(String text, String prefix, String suffix,
			scw.value.ValueFactory<String> propertyFactory) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		return new StringFormat.PropertyFactoryStringFormat(prefix, suffix,
				propertyFactory).format(text);
	}

	public static String format(String text, String prefix, String suffix,
			Map<?, ?> map) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		return new StringFormat.MapStringFormat(prefix, suffix, map)
				.format(text);
	}

	protected abstract String getValue(String key);

	public static class PropertyFactoryStringFormat extends StringFormat {
		private scw.value.ValueFactory<String> propertyFactory;

		public PropertyFactoryStringFormat(String prefix, String suffix,
				scw.value.ValueFactory<String> propertyFactory) {
			super(prefix, suffix);
			this.propertyFactory = propertyFactory;
		}

		@Override
		protected String getValue(String key) {
			if (propertyFactory == null) {
				return null;
			}

			return propertyFactory.getString(key);
		}
	}

	public static class MapStringFormat extends StringFormat {
		private Map<?, ?> map;

		public MapStringFormat(String prefix, String suffix, Map<?, ?> map) {
			super(prefix, suffix);
			this.map = map;
		}

		@Override
		protected String getValue(String key) {
			if (map == null) {
				return null;
			}

			Object v = map.get(map);
			return v == null ? null : v.toString();
		}
	}
}
