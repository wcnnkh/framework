package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferWriter;

@FunctionalInterface
public interface StringEncoder extends StringCoder, CharBufferEncoder<String> {

	@Override
	default String apply(String t) {
		return CharBufferEncoder.super.apply(t);
	}

	@Override
	default <E extends Throwable> void encode(String source,
			@NonNull BufferWriter<? super CharBuffer, ? extends E> writer) throws IOException, E {
		writeTo(CharBuffer.wrap(source), writer);
	}
	
}
