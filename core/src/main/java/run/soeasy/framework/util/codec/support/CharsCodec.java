package run.soeasy.framework.util.codec.support;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.codec.DecodeException;
import run.soeasy.framework.util.codec.EncodeException;

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
