package io.basc.framework.util.codec.support;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.MultipleCodec;
import io.basc.framework.util.codec.decode.BytesDecoder;
import io.basc.framework.util.codec.encode.BytesEncoder;

public interface BytesCodec
		extends MultipleCodec<byte[]>, BytesEncoder, BytesDecoder, ToBytesCodec<byte[]>, FromBytesCodec<byte[]> {

	default BytesCodec to(BytesCodec codec) {
		return new NestedBytesCodec(codec, this);
	}

	default BytesCodec from(BytesCodec codec) {
		return new NestedBytesCodec(this, codec);
	}

	@Override
	default BytesCodec multiple(int count) {
		return new NestedBytesCodec(null, this, count);
	}

	@Override
	default Codec<byte[], String> toBase64() {
		return ToBytesCodec.super.toBase64();
	}
}
