package scw.codec.support;

import scw.codec.Codec;
import scw.codec.encoder.BytesEncoder;

public interface BytesCodec<D> extends Codec<D, byte[]>, BytesEncoder<D> {

	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
