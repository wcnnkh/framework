package run.soeasy.framework.io;

import java.io.OutputStream;
import java.io.Writer;

public interface OutputFactoryWrapper<O extends OutputStream, E extends Writer, W extends OutputFactory<O, E>>
		extends OutputFactory<O, E>, OutputStreamFactoryWrapper<O, W>, WriterFactoryWrapper<E, W> {
	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}
}
