package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;

public interface ToBytesCodec<D> extends Codec<D, byte[]>, ToBytesEncoder<D>, FromBytesDecoder<D> {

	default Codec<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<D, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
