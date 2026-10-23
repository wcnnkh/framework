package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.DirectCodec;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;
import run.soeasy.framework.io.buffer.CharBufferReader;
import run.soeasy.framework.io.buffer.CharBufferWriter;

public interface DirectFormatter<T> extends Formatter<T>, DirectCodec<T, String> {

	@Override
	default CharBufferDecoder<T> decoder() {
		return new CharBufferDecoder<T>() {

			@Override
			public <E extends Throwable> T decode(@NonNull BufferReader<? super CharBuffer, ? extends E> source)
					throws IOException, E {
				try (Reader reader = new CharBufferReader(source)) {
					return DirectFormatter.this.parse(reader);
				}
			}
		};
	}

	@Override
	default CharBufferEncoder<T> encoder() {
		return new CharBufferEncoder<T>() {

			@Override
			public <E extends Throwable> void encode(T source,
					@NonNull BufferWriter<? super CharBuffer, ? extends E> writer) throws IOException, E {
				try (Writer target = new CharBufferWriter(writer)) {
					DirectFormatter.this.format(source, target);
				}
			}
		};
	}

	@Override
	default String encode(T source) {
		return encoder().apply(source);
	}

	@Override
	default T decode(String target) {
		return decoder().apply(target);
	}

	void format(T source, @NonNull Writer target) throws IOException;

	T parse(@NonNull Reader source) throws IOException;
}
