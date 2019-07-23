package scw.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This OutputStream writes all data to the famous <b>/dev/null</b>.
 * <p>
 * This output stream has no destination (file/socket etc.) and all bytes
 * written to it are ignored and lost.
 * 
 * @version $Id: NullOutputStream.java 1302056 2012-03-18 03:03:38Z ggregory $
 */
public class NullOutputStream extends OutputStream {

	/**
	 * A singleton.
	 */
	public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * 
	 * @param b
	 *            The bytes to write
	 * @param off
	 *            The start offset
	 * @param len
	 *            The number of bytes to write
	 */
	@Override
	public void write(byte[] b, int off, int len) {
		// to /dev/null
	}

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * 
	 * @param b
	 *            The byte to write
	 */
	@Override
	public void write(int b) {
		// to /dev/null
	}

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * 
	 * @param b
	 *            The bytes to write
	 * @throws IOException
	 *             never
	 */
	@Override
	public void write(byte[] b) throws IOException {
		// to /dev/null
	}

}
