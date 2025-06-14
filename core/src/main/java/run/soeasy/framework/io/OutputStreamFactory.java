package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface OutputStreamFactory<T extends OutputStream> {

	@NonNull
	Pipeline<T, IOException> getOutputStreamPipeline();

	default boolean isEncoded() {
		return false;
	}

	default <R extends Writer> OutputFactory<T, R> encode(
			@NonNull ThrowingFunction<? super T, ? extends R, IOException> pipeline) {
		return new StandardEncodedOutputStreamFactory<>(this, pipeline);
	}

	default OutputFactory<T, Writer> encode() {
		return new DefaultEncodedOutputStreamFactory<>(this);
	}

	default OutputFactory<T, Writer> encode(Charset charset) {
		return new StandardCharsetOutputStreamFactory<>(this, charset);
	}

	default OutputFactory<T, Writer> encode(CharsetEncoder charsetEncoder) {
		return new DefaultEncodedOutputStreamFactory<>(this, charsetEncoder);
	}

	default OutputFactory<T, Writer> encode(String charsetName) {
		return new StandardCharsetOutputStreamFactory<>(this, charsetName);
	}
}
