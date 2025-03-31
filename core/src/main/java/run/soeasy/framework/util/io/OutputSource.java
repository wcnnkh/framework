package run.soeasy.framework.util.io;

import java.io.OutputStream;
import java.io.Writer;

public interface OutputSource<O extends OutputStream, W extends Writer>
		extends OutputFactory<O, W>, OutputStreamSource<O>, WriterSource<W> {
	
}
