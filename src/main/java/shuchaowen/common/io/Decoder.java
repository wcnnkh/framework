package shuchaowen.common.io;

import java.io.IOException;
import java.io.InputStream;

public interface Decoder<T> {
	T decode(InputStream in) throws IOException;
}
