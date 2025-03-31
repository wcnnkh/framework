package run.soeasy.framework.util.io;

import java.io.OutputStream;
import java.io.Writer;

public interface OutputFactory<O extends OutputStream, W extends Writer>
		extends OutputStreamFactory<O>, WriterFactory<W> {
	@Override
	boolean isEncoded();
}
