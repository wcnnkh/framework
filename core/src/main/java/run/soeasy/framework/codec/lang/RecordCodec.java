package run.soeasy.framework.codec.lang;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.ToBytesCodec;
import run.soeasy.framework.io.Bits;
import run.soeasy.framework.io.IOUtils;

@RequiredArgsConstructor
public final class RecordCodec<D> implements ToBytesCodec<D> {
	@NonNull
	private final Codec<D, byte[]> codec;

	@Override
	public void encode(D source, OutputStream target) throws IOException, EncodeException {
		byte[] value = source == null ? null : codec.encode(source);
		Bits.writeInt(value == null ? 0 : value.length, target);
		if (value != null && value.length != 0) {
			target.write(value);
		}
	}

	@Override
	public D decode(InputStream source, int bufferSize) throws IOException, DecodeException, EOFException {
		int size = Bits.readInt(source);
		if (size == 0) {
			return null;
		}

		byte[] buff = new byte[size];
		IOUtils.readFully(source, buff);
		return codec.decode(buff);
	}

}
