package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.codec.DirectCodec;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferReaderChannel;
import run.soeasy.framework.io.buffer.BufferWriter;
import run.soeasy.framework.io.buffer.BufferWriterChanel;

public interface DirectSerializer<T> extends Serializer<T>, DirectCodec<T, byte[]> {

	@Override
	default ByteBufferEncoder<T> encoder() {
		return new ByteBufferEncoder<T>() {

			@Override
			public <E extends Throwable> void encode(T source,
					@NonNull BufferWriter<? super ByteBuffer, ? extends E> writer) throws IOException, E {
				try (WritableByteChannel output = new BufferWriterChanel(writer)) {
					this.encode(source, output);
				}
			}
 
			@Override
			public void encode(T source, @NonNull OutputStream writer) throws IOException {
				DirectSerializer.this.serialize(source, writer);
			}
		};
	}

	@Override
	default ByteBufferDecoder<T> decoder() {
		return new ByteBufferDecoder<T>() {

			@Override
			public <E extends Throwable> T decode(@NonNull BufferReader<? super ByteBuffer, ? extends E> source)
					throws IOException, E {
				try (ReadableByteChannel input = new BufferReaderChannel(source)) {
					return this.decode(input);
				}
			}

			@Override
			public T decode(@NonNull InputStream source) throws IOException {
				try {
					return DirectSerializer.this.deserialize(source);
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
			}
		};
	}

	@Override
	default byte[] encode(T source) {
		return encoder().apply(source);
	}

	@Override
	default T decode(byte[] target) {
		return decoder().apply(target);
	}

	void serialize(T source, @NonNull OutputStream target) throws IOException;

	T deserialize(@NonNull InputStream input) throws IOException, ClassNotFoundException;
}
