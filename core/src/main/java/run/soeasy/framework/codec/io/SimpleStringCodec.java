package run.soeasy.framework.codec.io;

import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CompositeCodec;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleStringCodec<S extends BufferTransporter<? extends CharBuffer>, T extends BufferTransporter<? extends CharBuffer>>
		extends CompositeCodec<String, String, SimpleStringEncoder<S>, SimpleStringDecoder<T>> implements StringCodec {

	public SimpleStringCodec(@NonNull S encodeTransporter, @NonNull T decodeTransporter) {
		this(new SimpleStringEncoder<>(encodeTransporter), new SimpleStringDecoder<>(decodeTransporter));
	}

	public SimpleStringCodec(@NonNull SimpleStringEncoder<S> encoder, @NonNull SimpleStringDecoder<T> decoder) {
		super(encoder, decoder);
	}
}
