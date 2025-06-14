package run.soeasy.framework.io;

import java.io.OutputStream;
import java.io.Writer;

public interface OutputFactory<O extends OutputStream, W extends Writer>
		extends OutputStreamFactory<O>, WriterFactory<W> {
	@Override
	boolean isEncoded();

	public static <O extends OutputStream, W extends Writer> OutputFactory<O, W> forFactory(
			OutputStreamFactory<? extends O> outputStreamFactory, WriterFactory<? extends W> writerFactory) {
		return new DefaultOutputFactory<>(outputStreamFactory, writerFactory);
	}
}
