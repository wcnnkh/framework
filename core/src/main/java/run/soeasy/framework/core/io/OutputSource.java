package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Pipeline;

public interface OutputSource<O extends OutputStream, W extends Writer>
		extends OutputFactory<O, W>, OutputStreamSource<O>, WriterSource<W> {
	public static interface OutputSourceWrapper<O extends OutputStream, E extends Writer, W extends OutputSource<O, E>>
			extends OutputSource<O, E>, OutputFactoryWrapper<O, E, W>, OutputStreamSourceWrapper<O, W>,
			WriterSourceWrapper<E, W> {
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
