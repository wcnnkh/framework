package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface DecodedInputStreamFactory<T extends InputStream, R extends Reader, W extends InputStreamFactory<T>>
		extends InputFactory<T, R>, InputStreamFactoryWrapper<T, W> {

	ThrowingFunction<? super T, ? extends R, IOException> getDecoder();

	@Override
	default @NonNull Pipeline<R, IOException> getReaderPipeline() {
		return getSource().getInputStreamPipeline().map(getDecoder());
	}

	@Override
	default boolean isDecoded() {
		return true;
	}
}