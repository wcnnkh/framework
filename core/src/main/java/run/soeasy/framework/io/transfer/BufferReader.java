package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.nio.Buffer;

/**
 * Reads data into a {@link Buffer}.
 *
 * <p>Contract:
 * <ul>
 *   <li>The buffer's position is advanced by the number of elements read</li>
 *   <li>The caller is responsible for {@code flip()} / {@code clear()}</li>
 * </ul>
 */
@FunctionalInterface
public interface BufferReader<B extends Buffer> {

	/**
	 * Reads data into the given buffer.
	 *
	 * @param buffer the buffer to fill
	 * @return number of elements read, or -1 if end of input
	 * @throws IOException if an I/O error occurs
	 */
	int read(B buffer) throws IOException;
}