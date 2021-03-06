package scw.codec.support;

import java.io.UnsupportedEncodingException;

import scw.codec.AbstractCodec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

public class StringCodec extends AbstractCodec<byte[], String> {
	private final String charsetName;

	public StringCodec(String charsetName) {
		this.charsetName = charsetName;
	}

	public String encode(byte[] source) throws EncodeException{
		try {
			return new String(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new EncodeException(charsetName, e);
		}
	}

	public byte[] decode(String source) throws DecodeException{
		try {
			return source.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new DecodeException(charsetName, e);
		}
	}
}
