package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface OutputSource<O extends OutputStream, W extends Writer>
		extends OutputFactory<O, W>, OutputStreamSource<O>, WriterSource<W> {
	boolean isWritable();

	@Override
	default @NonNull Pipeline<O, IOException> getOutputStreamPipeline() {
		return isWritable() ? OutputStreamSource.super.getOutputStreamPipeline() : Pipeline.empty();
	}

	@Override
	default @NonNull Pipeline<W, IOException> getWriterPipeline() {
		return isWritable() ? WriterSource.super.getWriterPipeline() : Pipeline.empty();
	}
}
