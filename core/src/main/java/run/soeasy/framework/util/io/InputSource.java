package run.soeasy.framework.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface InputSource<I extends InputStream, R extends Reader>
		extends InputFactory<I, R>, InputStreamSource<I>, ReaderSource<R> {
	public static class DefaultInputSource<I extends InputStream, S extends InputStreamSource<? extends I>, R extends Reader, T extends ReaderSource<? extends R>>
			extends DefaultInputFactory<I, S, R, T> implements InputSource<I, R> {

		public DefaultInputSource(S inputStream, T reader) {
			super(inputStream, reader);
		}

		@Override
		public I getInputStream() throws IOException {
			return inputStreamFactory == null ? null : inputStreamFactory.getInputStream();
		}

		@Override
		public R getReader() throws IOException {
			return readerFactory == null ? null : readerFactory.getReader();
		}

	}

	public static <I extends InputStream, R extends Reader> InputSource<I, R> forSource(
			InputStreamSource<I> inputStreamSource, ReaderSource<R> readerSource) {
		return new DefaultInputSource<>(inputStreamSource, readerSource);
	}
}
