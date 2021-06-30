package scw.io;

import java.io.IOException;
import java.io.OutputStream;

import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;

public interface WritableResource extends Resource, OutputStreamSource {
	/**
	 * Indicate whether the contents of this resource can be written via
	 * {@link #getOutputStream()}.
	 * <p>
	 * Will be {@code true} for typical resource descriptors; note that actual
	 * content writing may still fail when attempted. However, a value of
	 * {@code false} is a definitive indication that the resource content cannot be
	 * modified.
	 * 
	 * @see #getOutputStream()
	 * @see #isReadable()
	 */
	default boolean isWritable() {
		return true;
	}

	/**
	 * Return an {@link OutputStream} for the underlying resource, allowing to
	 * (over-)write its content.
	 * 
	 * @throws IOException if the stream could not be opened
	 * @see #getInputStream()
	 */
	OutputStream getOutputStream() throws IOException;

	default <T> T write(IoProcessor<OutputStream, ? extends T> processor) throws IOException {
		if (!exists()) {
			throw new NotFoundException("not found: " + getDescription());
		}

		if (!isWritable()) {
			throw new NotSupportedException("not write: " + getDescription());
		}

		OutputStream os = null;
		try {
			os = getOutputStream();
			return processor.process(os);
		} finally {
			if (os != null && !isOpen()) {
				os.close();
			}
		}
	}
}
