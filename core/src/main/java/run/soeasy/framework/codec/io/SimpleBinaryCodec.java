package run.soeasy.framework.codec.io;

import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CompositeCodec;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleBinaryCodec<S extends BufferTransporter<? extends ByteBuffer>, T extends BufferTransporter<? extends ByteBuffer>>
		extends CompositeCodec<byte[], byte[], SimpleBinaryCoder<S>, SimpleBinaryCoder<T>> implements BinaryCodec {

	public SimpleBinaryCodec(@NonNull S encodeTransporter, @NonNull T decodeTransporter) {
		this(new SimpleBinaryCoder<>(encodeTransporter), new SimpleBinaryCoder<>(decodeTransporter));
	}

	public SimpleBinaryCodec(@NonNull SimpleBinaryCoder<S> encoder, @NonNull SimpleBinaryCoder<T> decoder) {
		super(encoder, decoder);
	}
}
