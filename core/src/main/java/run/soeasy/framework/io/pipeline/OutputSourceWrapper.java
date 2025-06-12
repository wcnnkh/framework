package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface OutputSourceWrapper<O extends OutputStream, E extends Writer, W extends OutputSource<O, E>> extends
		OutputSource<O, E>, OutputFactoryWrapper<O, E, W>, OutputStreamSourceWrapper<O, W>, WriterSourceWrapper<E, W> {
	@Override
	default boolean isWritable() {
		return getSource().isWritable();
	}

	@Override
	default @NonNull Pipeline<O, IOException> getOutputStreamPipeline() {
		return getSource().getOutputStreamPipeline();
	}

	@Override
	default @NonNull Pipeline<E, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}
}