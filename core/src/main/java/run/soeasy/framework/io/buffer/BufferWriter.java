package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

public interface BufferWriter<B extends Buffer, E extends Throwable> {
	void write(B buffer) throws IOException, E;
}
