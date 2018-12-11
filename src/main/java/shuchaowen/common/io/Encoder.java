package shuchaowen.common.io;

import java.io.IOException;
import java.io.OutputStream;

public interface Encoder<T> {
	void encode(OutputStream out, T data) throws IOException;
}
