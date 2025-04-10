package run.soeasy.framework.util.codec.binary;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.codec.support.Base64;
import run.soeasy.framework.util.codec.support.HexCodec;

public interface ToBytesCodec<D> extends Codec<D, byte[]>, ToBytesEncoder<D>, FromBytesDecoder<D> {

	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
