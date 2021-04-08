package scw.codec.support;

import scw.codec.Codec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

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
