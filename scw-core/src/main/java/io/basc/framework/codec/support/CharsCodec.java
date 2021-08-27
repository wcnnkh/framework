package io.basc.framework.codec.support;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;

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
