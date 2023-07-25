package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.function.Processor;

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

	@Override
	default <T, E extends Throwable> T write(Processor<? super OutputStream, ? extends T, ? extends E> processor) throws IOException, E {
		if (!exists()) {
			throw new NotFoundException("not found: " + getDescription());
		}

		if (!isWritable()) {
			throw new UnsupportedException("not write: " + getDescription());
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
