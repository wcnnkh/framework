package scw.codec.support;

import scw.codec.AbstractCodec;
import scw.codec.Codec;

public abstract class AbstractToByteCodec<D> extends AbstractCodec<D, byte[]>{
	
	public Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	public Codec<D, String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}
}
