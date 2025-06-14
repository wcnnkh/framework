package run.soeasy.framework.io;

import java.io.InputStream;
import java.io.Reader;

public interface InputFactory<I extends InputStream, R extends Reader> extends InputStreamFactory<I>, ReaderFactory<R> {
	@Override
	boolean isDecoded();

	public static <I extends InputStream, R extends Reader> InputFactory<I, R> forFactory(
			InputStreamFactory<? extends I> inputStreamFactory, ReaderFactory<? extends R> readerFactory) {
		return new DefaultInputFactory<>(inputStreamFactory, readerFactory);
	}
}
