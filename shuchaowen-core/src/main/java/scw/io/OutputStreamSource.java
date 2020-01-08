package scw.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamSource {
	OutputStream getOutputStream() throws IOException;
}
