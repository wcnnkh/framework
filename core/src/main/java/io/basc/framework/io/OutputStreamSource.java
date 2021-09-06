package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import io.basc.framework.util.stream.Callback;
import io.basc.framework.util.stream.Processor;

public interface OutputStreamSource {
	OutputStream getOutputStream() throws IOException;

	/**
	 * Return a {@link WritableByteChannel}.
	 * <p>
	 * It is expected that each call creates a <i>fresh</i> channel.
	 * <p>
	 * The default implementation returns {@link Channels#newChannel(OutputStream)}
	 * with the result of {@link #getOutputStream()}.
	 * 
	 * @return the byte channel for the underlying resource (must not be
	 *         {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't
	 *                                       exist
	 * @throws IOException                   if the content channel could not be
	 *                                       opened
	 * @see #getOutputStream()
	 */
	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}

	default <T, E extends Throwable> T write(Processor<OutputStream, ? extends T, E> processor) throws IOException, E {
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

	/**
	 * 一般不需要重写此方法，默认调用的是{@see OutputStreamSource#write(IoProcessor)}
	 * 
	 * @param callback
	 * @throws IOException
	 */
	default <E extends Throwable> void write(Callback<OutputStream, E> callback) throws IOException, E {
		write((is) -> {
			callback.call(is);
			return null;
		});
	}
}
