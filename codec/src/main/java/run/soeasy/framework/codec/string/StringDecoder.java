package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleDecoder;
import run.soeasy.framework.core.function.ThrowingConsumer;

@FunctionalInterface
public interface StringDecoder
		extends FromStringDecoder<CharSequence>, ToStringDecoder<CharSequence>, MultipleDecoder<CharSequence> {

	@Override
	default CharSequence decode(CharSequence source) throws CodecException {
		return FromStringDecoder.super.decode(source);
	}

	@Override
	default CharSequence decodeFromReadable(@NonNull Readable source, @NonNull CharBuffer buffer)
			throws CodecException, IOException {
		StringBuilder sb = new StringBuilder();
		decode(source, buffer, sb);
		return sb;
	}

	@Override
	default <X extends Exception> void decodeToConsumer(CharSequence source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X {
		decode(CharBuffer.wrap(source), buffer, buffer);
	}

	default void decode(@NonNull Readable source, @NonNull CharBuffer buffer, @NonNull Appendable appendable)
			throws CodecException, IOException {
		decode(source, buffer, appendable::append);
	}

	<X extends Exception> void decode(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X;
}
