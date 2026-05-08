package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleEncoder;
import run.soeasy.framework.core.function.ThrowingConsumer;

public interface StringEncoder
		extends FromStringEncoder<CharSequence>, ToStringEncoder<CharSequence>, MultipleEncoder<CharSequence> {

	@Override
	default CharSequence encode(@NonNull CharSequence source) throws CodecException {
		return FromStringEncoder.super.encode(source);
	}

	@Override
	default CharSequence encodeFromReadable(@NonNull Readable readableSource, @NonNull CharBuffer buffer)
			throws IOException, CodecException {
		StringBuilder sb = new StringBuilder();
		encode(readableSource, buffer, sb);
		return sb;
	}

	default void encode(@NonNull Readable source, @NonNull CharBuffer buffer, @NonNull Appendable appendable)
			throws CodecException, IOException {
		encode(source, buffer, appendable::append);
	}

	@Override
	default <X extends Exception> void encodeToConsumer(CharSequence source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X {
		encode(CharBuffer.wrap(source), buffer, consumer);
	}

	<X extends Exception> void encode(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X;
}
