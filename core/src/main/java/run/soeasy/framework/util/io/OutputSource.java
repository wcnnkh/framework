package run.soeasy.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface OutputSource<O extends OutputStream, W extends Writer>
		extends OutputFactory<O, W>, OutputStreamSource<O>, WriterSource<W> {
	public static class DefaultOutputSource<O extends OutputStream, S extends OutputStreamSource<? extends O>, W extends Writer, T extends WriterSource<? extends W>>
			extends DefaultOutputFactory<O, S, W, T> implements OutputSource<O, W> {

		public DefaultOutputSource(S outputStreamFactory, T writerFactory) {
			super(outputStreamFactory, writerFactory);
		}

		@Override
		public O getOutputStream() throws IOException {
			return outputStreamFactory == null ? null : outputStreamFactory.getOutputStream();
		}

		@Override
		public W getWriter() throws IOException {
			return writerFactory == null ? null : writerFactory.getWriter();
		}

	}

	public static <O extends OutputStream, W extends Writer> OutputSource<O, W> forSource(
			OutputStreamSource<? extends O> outputStreamSource, WriterSource<? extends W> writerSource) {
		return new DefaultOutputSource<>(outputStreamSource, writerSource);
	}
}
