package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

public interface BufferReader<B extends Buffer, E extends Throwable> {
	int read(B buffer) throws IOException, E;
}
