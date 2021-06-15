package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Simple interface for objects that are sources for an {@link InputStream}.
 */
@FunctionalInterface
public interface InputStreamSource {

	/**
	 * Return an {@link InputStream} for the content of an underlying resource.
	 * <p>
	 * It is expected that each call creates a <i>fresh</i> stream.
	 * <p>
	 * This requirement is particularly important when you consider an API such as
	 * JavaMail, which needs to be able to read the stream multiple times when
	 * creating mail attachments. For such a use case, it is <i>required</i> that
	 * each {@code getInputStream()} call returns a fresh stream.
	 * 
	 * @return the input stream for the underlying resource (must not be
	 *         {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't
	 *                                       exist
	 * @throws IOException                   if the content stream could not be
	 *                                       opened
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * This implementation returns {@link Channels#newChannel(InputStream)}
	 * with the result of {@link #getInputStream()}.
	 * <p>This is the same as in {@link Resource}'s corresponding default method
	 * but mirrored here for efficient JVM-level dispatching in a class hierarchy.
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	default <T> T read(IoProcessor<InputStream, ? extends T> processor) throws IOException {
		InputStream is = null;
		try {
			is = getInputStream();
			return processor.process(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	default byte[] getBytes() throws IOException {
		return read((is)->{
			return IOUtils.toByteArray(is);
		});
	}
}
