package io.basc.framework.codec;

import io.basc.framework.codec.decode.BytesDecoder;
import io.basc.framework.codec.encode.BytesEncoder;

public interface BytesCodec extends MultipleCodec<byte[]>, BytesEncoder, BytesDecoder {

	default BytesCodec to(BytesCodec codec) {
		return new NestedBytesCodec(codec, this);
	}

	default BytesCodec from(BytesCodec codec) {
		return new NestedBytesCodec(this, codec);
	}

	@Override
	default byte[] encode(byte[] source) throws EncodeException {
		return MultipleCodec.super.encode(source);
	}

	@Override
	default byte[] decode(byte[] source) throws DecodeException {
		return MultipleCodec.super.decode(source);
	}
}
