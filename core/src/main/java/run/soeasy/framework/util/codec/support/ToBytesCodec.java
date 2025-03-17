package run.soeasy.framework.util.codec.support;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.codec.decode.FromBytesDecoder;
import run.soeasy.framework.util.codec.encode.ToBytesEncoder;

public interface ToBytesCodec<D> extends Codec<D, byte[]>, ToBytesEncoder<D>, FromBytesDecoder<D> {

	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
