package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferReader;

@FunctionalInterface
public interface StringDecoder extends StringCoder, CharBufferDecoder<String> {

	@Override
	default String apply(String source) {
		return CharBufferDecoder.super.apply(source);
	}

	@Override
	default <E extends Throwable> String decode(@NonNull BufferReader<? super CharBuffer, ? extends E> source)
			throws IOException, E {
		return toCharSequence(source).toString();
	}

}
