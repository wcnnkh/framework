package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface EncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
		extends OutputStreamFactoryWrapper<T, W>, OutputFactory<T, R> {
	ThrowingFunction<? super T, ? extends R, IOException> getEncoder();

	@Override
	default @NonNull Pipeline<R, IOException> getWriterPipeline() {
		return getSource().getOutputStreamPipeline().map(getEncoder());
	}

	@Override
	default boolean isEncoded() {
		return true;
	}
}
