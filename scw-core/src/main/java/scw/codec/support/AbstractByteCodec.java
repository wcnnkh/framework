package scw.codec.support;

import scw.codec.AbstractCodec;
import scw.codec.Codec;

public abstract class AbstractByteCodec extends AbstractCodec<byte[], byte[]>{
	
	public Codec<byte[], String> toBase64() {
		return to(Base64.DEFAULT);
	}

	public Codec<byte[], String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}
}
