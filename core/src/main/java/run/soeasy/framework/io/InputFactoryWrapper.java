package run.soeasy.framework.io;

import java.io.InputStream;
import java.io.Reader;

public interface InputFactoryWrapper<I extends InputStream, R extends Reader, W extends InputFactory<I, R>>
		extends InputFactory<I, R>, InputStreamFactoryWrapper<I, W>, ReaderFactoryWrapper<R, W> {

	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}

}
