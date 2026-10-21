package run.soeasy.framework.codec.io;

import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CompositeCodec;
import run.soeasy.framework.io.buffer.BufferTransporter;

public class SimpleStringCodec<S extends BufferTransporter<? extends CharBuffer>, T extends BufferTransporter<? extends CharBuffer>>
		extends CompositeCodec<String, String, SimpleStringCoder<S>, SimpleStringCoder<T>> implements StringCodec {

	public SimpleStringCodec(@NonNull S encodeTransporter, @NonNull T decodeTransporter) {
		this(new SimpleStringCoder<>(encodeTransporter), new SimpleStringCoder<>(decodeTransporter));
	}

	public SimpleStringCodec(@NonNull SimpleStringCoder<S> encoder, @NonNull SimpleStringCoder<T> decoder) {
		super(encoder, decoder);
	}
}
