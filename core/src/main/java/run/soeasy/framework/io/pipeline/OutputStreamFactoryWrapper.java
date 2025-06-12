package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface OutputStreamFactoryWrapper<T extends OutputStream, W extends OutputStreamFactory<T>>
		extends OutputStreamFactory<T>, Wrapper<W> {
	@Override
	default Pipeline<T, IOException> getOutputStreamPipeline() {
		return getSource().getOutputStreamPipeline();
	}

	@Override
	default <R extends Writer> OutputFactory<T, R> encode(
			@NonNull ThrowingFunction<? super T, ? extends R, IOException> pipeline) {
		return getSource().encode(pipeline);
	}

	@Override
	default OutputFactory<T, Writer> encode() {
		return getSource().encode();
	}

	@Override
	default OutputFactory<T, Writer> encode(Charset charset) {
		return getSource().encode(charset);
	}

	@Override
	default OutputFactory<T, Writer> encode(CharsetEncoder charsetEncoder) {
		return getSource().encode(charsetEncoder);
	}

	@Override
	default OutputFactory<T, Writer> encode(String charsetName) {
		return getSource().encode(charsetName);
	}

	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}
}