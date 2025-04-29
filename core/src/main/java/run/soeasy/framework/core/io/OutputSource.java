package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Source;

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
		default @NonNull Source<O, IOException> getOutputStreamPipeline() {
			return getSource().getOutputStreamPipeline();
		}

		@Override
		default @NonNull Source<E, IOException> getWriterPipeline() {
			return getSource().getWriterPipeline();
		}
	}

	boolean isWritable();

	@Override
	default @NonNull Source<O, IOException> getOutputStreamPipeline() {
		return isWritable() ? OutputStreamSource.super.getOutputStreamPipeline() : Source.empty();
	}

	@Override
	default @NonNull Source<W, IOException> getWriterPipeline() {
		return isWritable() ? WriterSource.super.getWriterPipeline() : Source.empty();
	}
}
