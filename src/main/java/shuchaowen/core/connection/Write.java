package shuchaowen.core.connection;

import java.io.IOException;
import java.io.OutputStream;

public interface Write {
	void write(OutputStream outputStream) throws IOException;
}
