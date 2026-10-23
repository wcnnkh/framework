package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

/**
 * Writes data from a {@link Buffer}.
 *
 * <p>
 * Contract:
 * <ul>
 * <li>The buffer's position is advanced by the number of elements written</li>
 * <li>The caller is responsible for {@code flip()} / {@code clear()}</li>
 * <li>No partial write semantics are guaranteed unless otherwise specified</li>
 * </ul>
 */
@FunctionalInterface
public interface BufferWriter<B extends Buffer, E extends Throwable> {

	/**
	 * Writes the remaining contents of the given buffer.
	 *
	 * @param buffer the buffer to write
	 * @throws IOException if an I/O error occurs
	 */
	void write(B buffer) throws IOException, E;
}