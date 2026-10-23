package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.codec.zip.Gzip;

public interface Serializer<T> extends Codec<T, byte[]> {
	@Override
	ByteBufferEncoder<T> encoder();

	@Override
	ByteBufferDecoder<T> decoder();

	default Codec<T, byte[]> gzip() {
		return to(Gzip.DEFAULT);
	}

	default Codec<T, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Codec<T, String> toHex() {
		return to(HexCodec.DEFAULT);
	}

	default T clone(T source) {
		if (source == null) {
			return null;
		}
		byte[] data = encode(source);
		return decode(data);
	}
}
