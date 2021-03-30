package scw.codec.support;

import scw.codec.Codec;

public interface BytesCodec<D> extends Codec<D, byte[]>{
	
	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}
}
