package io.basc.framework.util.io;

import java.io.IOException;

public interface ByteArrayConsumer {
	void accept(byte[] array, int offset, int len) throws IOException;
}
