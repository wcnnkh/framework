package run.soeasy.framework.codec.commons;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.codec.zip.Gzip;

public interface ToBinaryCodec<S> extends Codec<S, byte[]> {
	default Codec<S, byte[]> gzip() {
		return to(Gzip.DEFAULT);
	}

	default Codec<S, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<S, String> toHex() {
		return to(HexCodec.DEFAULT);
	}
}
