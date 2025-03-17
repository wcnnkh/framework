package run.soeasy.framework.util.codec.support;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.codec.MultipleCodec;
import run.soeasy.framework.util.codec.decode.BytesDecoder;
import run.soeasy.framework.util.codec.encode.BytesEncoder;

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
