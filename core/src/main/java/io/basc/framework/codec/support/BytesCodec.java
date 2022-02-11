package io.basc.framework.codec.support;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.encode.ToBytesEncoder;

public interface BytesCodec<D> extends Codec<D, byte[]>, ToBytesEncoder<D> {

	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
