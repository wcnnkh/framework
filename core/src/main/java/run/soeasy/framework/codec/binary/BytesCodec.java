package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.MultipleCodec;

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
