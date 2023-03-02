package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;

public interface OutputStreamSource {
	OutputStream getOutputStream() throws IOException;

	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}

	default <T, E extends Throwable> T write(Processor<? super OutputStream, ? extends T, ? extends E> processor)
			throws IOException, E {
		OutputStream os = null;
		try {
			os = getOutputStream();
			return processor.process(os);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	default <E extends Throwable> void produce(ConsumeProcessor<? super OutputStream, ? extends E> processor)
			throws IOException, E {
		write((is) -> {
			processor.process(is);
			return null;
		});
	}
}
