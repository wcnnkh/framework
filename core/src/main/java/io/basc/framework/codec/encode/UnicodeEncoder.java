package io.basc.framework.codec.encode;

import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.SimpleMultipleEncoder;

public class UnicodeEncoder implements SimpleMultipleEncoder<CharSequence> {
	private static final String EMPTY = "";
	public static final UnicodeEncoder DEFAULT = new UnicodeEncoder();

	@Override
	public CharSequence encode(CharSequence source) throws EncodeException {
		if (source == null) {
			return null;
		}

		int len = source.length();
		if (len == 0) {
			return EMPTY;
		}

		StringBuilder builder = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char ch = source.charAt(i);
			if (ch < 256) {
				builder.append(ch);
			} else {
				builder.append("\\u" + Integer.toHexString(ch & 0xffff));
			}
		}
		return builder;
	}
}
