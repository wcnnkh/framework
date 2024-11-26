package io.basc.framework.util.codec.support;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;

public class CharsCodec implements Codec<String, char[]>{
	public static final CharsCodec DEFAULT = new CharsCodec();

	@Override
	public char[] encode(String source) throws EncodeException {
		return source.toCharArray();
	}

	@Override
	public String decode(char[] source) throws DecodeException {
		return new String(source);
	}

}
