package run.soeasy.framework.codec.lang;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;

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
