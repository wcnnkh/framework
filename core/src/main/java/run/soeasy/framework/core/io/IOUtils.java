package run.soeasy.framework.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

public final class IOUtils {
	// NOTE: This class is focussed on InputStream, OutputStream, Reader and
	// Writer. Each method should take at least one of these as a parameter,
	// or return one of them.

	private static final int EOF = -1;
	/**
	 * The Unix directory separator character.
	 */
	public static final char DIR_SEPARATOR_UNIX = '/';
	/**
	 * The Windows directory separator character.
	 */
	public static final char DIR_SEPARATOR_WINDOWS = '\\';
	/**
	 * The system directory separator character.
	 */
	public static final char DIR_SEPARATOR = File.separatorChar;
	/**
	 * The Unix line separator string.
	 */
	public static final String LINE_SEPARATOR_UNIX = "\n";
	/**
	 * The Windows line separator string.
	 */
	public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

	/**
	 * The default buffer size ({@value} ) to use for
	 */
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * The system line separator string.
	 */
	public static final String LINE_SEPARATOR;

	static {
		// avoid security issues
		StringWriter buf = new StringWriter(4);
		PrintWriter out = new PrintWriter(buf);
		out.println();
		LINE_SEPARATOR = buf.toString();
		out.close();
	}

	private static final int DEFAULT_READ_BUFFER_SIZE = 256;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	/**
	 * The default buffer size to use for the skip() methods.
	 */
	private static final int SKIP_BUFFER_SIZE = 2048;

	// Allocated in the relevant skip method if necessary.
	/*
	 * N.B. no need to synchronize these because: - we don't care if the buffer is
	 * created multiple times (the data is ignored) - we always use the same size
	 * buffer, so if it it is recreated it will still be OK (if the buffer size were
	 * variable, we would need to synch. to ensure some other thread did not create
	 * a smaller one)
	 */
	private static char[] SKIP_CHAR_BUFFER;
	private static byte[] SKIP_BYTE_BUFFER;

	/**
	 * Instances should NOT be constructed in standard programming.
	 */
	private IOUtils() {
	}

	public static InputStream limitedInputStream(final InputStream is, final long limit) {
		return new LimitedInputStream(is, limit);
	}

	/**
	 * write.
	 * 
	 * @param is InputStream instance
	 * @param os OutputStream instance
	 * @return count
	 * @throws IOException
	 */
	public static long write(InputStream is, OutputStream os) throws IOException {
		return write(is, os, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * write.
	 * 
	 * @param is         InputStream instance
	 * @param os         OutputStream instance
	 * @param bufferSize buffer size
	 * @return count
	 * @throws IOException
	 */
	public static long write(InputStream is, OutputStream os, int bufferSize) throws IOException {
		return copy(is, os, new byte[bufferSize]);
	}

	/**
	 * write lines.
	 * 
	 * @param os    output stream.
	 * @param lines lines.
	 * @throws IOException
	 */
	public static void writeLines(OutputStream os, String[] lines) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
		try {
			for (String line : lines) {
				writer.println(line);
			}
			writer.flush();
		} finally {
			writer.close();
		}
	}

	/**
	 * write lines.
	 * 
	 * @param file  file.
	 * @param lines lines.
	 * @throws IOException
	 */
	public static void writeLines(File file, String[] lines) throws IOException {
		if (file == null) {
			throw new IOException("File is null.");
		}
		writeLines(new FileOutputStream(file), lines);
	}

	/**
	 * append lines.
	 * 
	 * @param file  file.
	 * @param lines lines.
	 * @throws IOException
	 */
	public static void appendLines(File file, String[] lines) throws IOException {
		if (file == null) {
			throw new IOException("File is null.");
		}
		writeLines(new FileOutputStream(file, true), lines);
	}

	public static String read(Reader reader, int buffSize) throws IOException {
		StringBuilder sb = new StringBuilder(buffSize);
		char[] buff = new char[buffSize];
		int len;
		while ((len = reader.read(buff)) != -1) {
			sb.append(buff, 0, len);
		}
		return sb.toString();
	}

	public static String read(Reader reader) throws IOException {
		return read(reader, DEFAULT_READ_BUFFER_SIZE);
	}

	public static void close(Closeable... closeables) throws IOException {
		if (closeables == null) {
			return;
		}

		IOException exception = null;
		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}

			try {
				closeable.close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				} else {
					exception.addSuppressed(e);
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	public static void closeQuietly(Closeable... closeables) {
		if (closeables == null) {
			return;
		}

		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}

			try {
				closeable.close();
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Returns the given reader if it is a {@link BufferedReader}, otherwise creates
	 * a toBufferedReader for the given reader.
	 * 
	 * @param reader the reader to wrap or return
	 * @return the given reader or a new {@link BufferedReader} for the given reader
	 */
	public static BufferedReader toBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}

	// read toByteArray
	// -----------------------------------------------------------------------
	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	/**
	 * Get contents of an <code>InputStream</code> as a <code>byte[]</code>. Use
	 * this method instead of <code>toByteArray(InputStream)</code> when
	 * <code>InputStream</code> size is known. <b>NOTE:</b> the method checks that
	 * the length can safely be cast to an int without truncation before using
	 * {@link IOUtils#toByteArray(java.io.InputStream, int)} to read into the byte
	 * array. (Arrays can have no more than Integer.MAX_VALUE entries anyway)
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @param size  the size of <code>InputStream</code>
	 * @return the requested byte array
	 * @throws IOException              if an I/O error occurs or
	 *                                  <code>InputStream</code> size differ from
	 *                                  parameter size
	 * @throws IllegalArgumentException if size is less than zero or size is greater
	 *                                  than Integer.MAX_VALUE
	 * @see IOUtils#toByteArray(java.io.InputStream, int)
	 */
	public static byte[] toByteArray(InputStream input, long size) throws IOException {

		if (size > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
		}

		return toByteArray(input, (int) size);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>. Use
	 * this method instead of <code>toByteArray(InputStream)</code> when
	 * <code>InputStream</code> size is known
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @param size  the size of <code>InputStream</code>
	 * @return the requested byte array
	 * @throws IOException              if an I/O error occurs or
	 *                                  <code>InputStream</code> size differ from
	 *                                  parameter size
	 * @throws IllegalArgumentException if size is less than zero
	 */
	public static byte[] toByteArray(InputStream input, int size) throws IOException {

		if (size < 0) {
			throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
		}

		if (size == 0) {
			return new byte[0];
		}

		byte[] data = new byte[size];
		int offset = 0;
		int readed;

		while (offset < size && (readed = input.read(data, offset, size - offset)) != EOF) {
			offset += readed;
		}

		if (offset != size) {
			throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
		}

		return data;
	}

	// read char[]
	// -----------------------------------------------------------------------
	/**
	 * Get the contents of an <code>InputStream</code> as a character array using
	 * the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param is the <code>InputStream</code> to read from
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static char[] toCharArray(InputStream is) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output);
		return output.toCharArray();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a character array using
	 * the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param is       the <code>InputStream</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static char[] toCharArray(InputStream is, String encoding) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output, encoding);
		return output.toCharArray();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a character array.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * 
	 * @param input the <code>Reader</code> to read from
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static char[] toCharArray(Reader input) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy(input, sw);
		return sw.toCharArray();
	}

	// read toString
	// -----------------------------------------------------------------------
	/**
	 * Get the contents of an <code>InputStream</code> as a String using the default
	 * character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static String toString(InputStream input) throws IOException {
		return toString(input, null);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String using the
	 * specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input    the <code>InputStream</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static String toString(@NonNull InputStream input, String encoding) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(input, out);
		return StringUtils.hasText(encoding) ? out.toString(encoding) : out.toString();
	}

	// readLines
	// -----------------------------------------------------------------------
	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input the <code>InputStream</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException if the input is null
	 */
	public static Stream<String> readLines(InputStream input) {
		InputStreamReader reader = new InputStreamReader(input);
		return readLines(reader).onClose(() -> closeQuietly(reader));
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one
	 * entry per line, using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input    the <code>InputStream</code> to read from, not null
	 * @param encoding the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws UnsupportedEncodingException
	 * @throws NullPointerException         if the input is null
	 */
	public static Stream<String> readLines(@NonNull InputStream input, String encoding)
			throws UnsupportedEncodingException {
		if (encoding == null) {
			return readLines(input);
		} else {
			InputStreamReader reader = new InputStreamReader(input, encoding);
			return readLines(reader).onClose(() -> closeQuietly(reader));
		}
	}

	/**
	 * Get the contents of a <code>Reader</code> as a list of Strings, one entry per
	 * line.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param reader the <code>Reader</code> to read from, not null
	 * @return the cursor of Strings, never null
	 * @throws NullPointerException if the input is null
	 */
	public static Stream<String> readLines(Reader reader) {
		if (reader instanceof BufferedReader) {
			return readLines((BufferedReader) reader);
		}

		BufferedReader bufferedReader = new BufferedReader(reader);
		return readLines(bufferedReader).onClose(() -> closeQuietly(bufferedReader));
	}

	public static Stream<String> readLines(BufferedReader bufferedReader) {
		LineIterator iterator = new LineIterator(bufferedReader);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	// write byte[]
	// -----------------------------------------------------------------------
	/**
	 * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code>.
	 * 
	 * @param data   the byte array to write, do not modify during output, null
	 *               ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(byte[] data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
	 * using the default character encoding of the platform.
	 * <p>
	 * This method uses {@link String#String(byte[])}.
	 * 
	 * @param data   the byte array to write, do not modify during output, null
	 *               ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(byte[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(new String(data));
		}
	}

	/**
	 * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
	 * using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link String#String(byte[], String)}.
	 * 
	 * @param data     the byte array to write, do not modify during output, null
	 *                 ignored
	 * @param output   the <code>Writer</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(byte[] data, Writer output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(new String(data, encoding));
			}
		}
	}

	// write char[]
	// -----------------------------------------------------------------------
	/**
	 * Writes chars from a <code>char[]</code> to a <code>Writer</code> using the
	 * default character encoding of the platform.
	 * 
	 * @param data   the char array to write, do not modify during output, null
	 *               ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(char[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes chars from a <code>char[]</code> to bytes on an
	 * <code>OutputStream</code>.
	 * <p>
	 * This method uses {@link String#String(char[])} and {@link String#getBytes()}.
	 * 
	 * @param data   the char array to write, do not modify during output, null
	 *               ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(char[] data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(new String(data).getBytes());
		}
	}

	/**
	 * Writes chars from a <code>char[]</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link String#String(char[])} and
	 * {@link String#getBytes(String)}.
	 * 
	 * @param data     the char array to write, do not modify during output, null
	 *                 ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(char[] data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(new String(data).getBytes(encoding));
			}
		}
	}

	// write CharSequence
	// -----------------------------------------------------------------------
	/**
	 * Writes chars from a <code>CharSequence</code> to a <code>Writer</code>.
	 * 
	 * @param data   the <code>CharSequence</code> to write, null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(CharSequence data, Writer output) throws IOException {
		if (data != null) {
			write(data.toString(), output);
		}
	}

	/**
	 * Writes chars from a <code>CharSequence</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform.
	 * <p>
	 * This method uses {@link String#getBytes()}.
	 * 
	 * @param data   the <code>CharSequence</code> to write, null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(CharSequence data, OutputStream output) throws IOException {
		if (data != null) {
			write(data.toString(), output);
		}
	}

	/**
	 * Writes chars from a <code>CharSequence</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link String#getBytes(String)}.
	 * 
	 * @param data     the <code>CharSequence</code> to write, null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(CharSequence data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			write(data.toString(), output, encoding);
		}
	}

	// write String
	// -----------------------------------------------------------------------
	/**
	 * Writes chars from a <code>String</code> to a <code>Writer</code>.
	 * 
	 * @param data   the <code>String</code> to write, null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(String data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform.
	 * <p>
	 * This method uses {@link String#getBytes()}.
	 * 
	 * @param data   the <code>String</code> to write, null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(String data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(data.getBytes());
		}
	}

	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link String#getBytes(String)}.
	 * 
	 * @param data     the <code>String</code> to write, null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void write(String data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(data.getBytes(encoding));
			}
		}
	}

	// writeLines
	// -----------------------------------------------------------------------
	/**
	 * Writes the <code>toString()</code> value of each item in a collection to an
	 * <code>OutputStream</code> line by line, using the default character encoding
	 * of the platform and the specified line ending.
	 *
	 * @param lines      the lines to write, null entries produce blank lines
	 * @param lineEnding the line separator to use, null is system default
	 * @param output     the <code>OutputStream</code> to write to, not null, not
	 *                   closed
	 * @throws NullPointerException if the output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output) throws IOException {
		if (lines == null) {
			return;
		}
		if (lineEnding == null) {
			lineEnding = LINE_SEPARATOR;
		}
		for (Object line : lines) {
			if (line != null) {
				output.write(line.toString().getBytes());
			}
			output.write(lineEnding.getBytes());
		}
	}

	/**
	 * Writes the <code>toString()</code> value of each item in a collection to an
	 * <code>OutputStream</code> line by line, using the specified character
	 * encoding and the specified line ending.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 *
	 * @param lines      the lines to write, null entries produce blank lines
	 * @param lineEnding the line separator to use, null is system default
	 * @param output     the <code>OutputStream</code> to write to, not null, not
	 *                   closed
	 * @param encoding   the encoding to use, null means platform default
	 * @throws NullPointerException if the output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, String encoding)
			throws IOException {
		if (encoding == null) {
			writeLines(lines, lineEnding, output);
		} else {
			if (lines == null) {
				return;
			}
			if (lineEnding == null) {
				lineEnding = LINE_SEPARATOR;
			}
			for (Object line : lines) {
				if (line != null) {
					output.write(line.toString().getBytes(encoding));
				}
				output.write(lineEnding.getBytes(encoding));
			}
		}
	}

	/**
	 * Writes the <code>toString()</code> value of each item in a collection to a
	 * <code>Writer</code> line by line, using the specified line ending.
	 *
	 * @param lines      the lines to write, null entries produce blank lines
	 * @param lineEnding the line separator to use, null is system default
	 * @param writer     the <code>Writer</code> to write to, not null, not closed
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void writeLines(Collection<?> lines, String lineEnding, Writer writer) throws IOException {
		if (lines == null) {
			return;
		}
		if (lineEnding == null) {
			lineEnding = LINE_SEPARATOR;
		}
		for (Object line : lines) {
			if (line != null) {
				writer.write(line.toString());
			}
			writer.write(lineEnding);
		}
	}

	// copy from InputStream
	/**
	 * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 * 
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(InputStream input, OutputStream output) throws IOException {
		return copy(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * 
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @param buffer the buffer to use for the copy
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>, optionally skipping input bytes.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 * 
	 * @param input       the <code>InputStream</code> to read from
	 * @param output      the <code>OutputStream</code> to write to
	 * @param inputOffset : number of bytes to skip from input before copying -ve
	 *                    values are ignored
	 * @param length      : number of bytes to copy. -ve means all
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(InputStream input, OutputStream output, long inputOffset, long length) throws IOException {
		return copy(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>, optionally skipping input bytes.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * 
	 * @param input       the <code>InputStream</code> to read from
	 * @param output      the <code>OutputStream</code> to write to
	 * @param inputOffset : number of bytes to skip from input before copying -ve
	 *                    values are ignored
	 * @param length      : number of bytes to copy. -ve means all
	 * @param buffer      the buffer to use for the copy
	 *
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(InputStream input, OutputStream output, final long inputOffset, final long length,
			byte[] buffer) throws IOException {
		if (inputOffset > 0) {
			skipFully(input, inputOffset);
		}
		if (length == 0) {
			return 0;
		}
		final int bufferLength = buffer.length;
		int bytesToRead = bufferLength;
		if (length > 0 && length < bufferLength) {
			bytesToRead = (int) length;
		}
		int read;
		long totalRead = 0;
		while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
			output.write(buffer, 0, read);
			totalRead += read;
			if (length > 0) { // only adjust length if not reading to the end
				// Note the cast must work because buffer.length is an integer
				bytesToRead = (int) Math.min(length - totalRead, bufferLength);
			}
		}
		return totalRead;
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a <code>Writer</code>
	 * using the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * This method uses {@link InputStreamReader}.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a <code>Writer</code>
	 * using the specified character encoding.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link InputStreamReader}.
	 *
	 * @param input    the <code>InputStream</code> to read from
	 * @param output   the <code>Writer</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static void copy(InputStream input, Writer output, String encoding) throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(in, output);
		}
	}

	// copy from Reader
	// -----------------------------------------------------------------------

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a
	 * <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(Reader input, Writer output) throws IOException {
		return copy(input, output, new char[DEFAULT_BUFFER_SIZE]);
	}

	public static long append(Reader input, Writer output) throws IOException {
		return append(input, output, new char[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a
	 * <code>Writer</code>.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * <p>
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @param buffer the buffer to be used for the copy
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(Reader input, Writer output, char[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static long append(Reader input, Writer output, char[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.append(new String(buffer, 0, n));
			count += n;
		}
		return count;
	}

	/**
	 * Copy some or all chars from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>, optionally skipping input chars.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 * 
	 * @param input       the <code>Reader</code> to read from
	 * @param output      the <code>Writer</code> to write to
	 * @param inputOffset : number of chars to skip from input before copying -ve
	 *                    values are ignored
	 * @param length      : number of chars to copy. -ve means all
	 * @return the number of chars copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(Reader input, Writer output, final long inputOffset, final long length) throws IOException {
		return copy(input, output, inputOffset, length, new char[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy some or all chars from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>, optionally skipping input chars.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * <p>
	 * 
	 * @param input       the <code>Reader</code> to read from
	 * @param output      the <code>Writer</code> to write to
	 * @param inputOffset : number of chars to skip from input before copying -ve
	 *                    values are ignored
	 * @param length      : number of chars to copy. -ve means all
	 * @param buffer      the buffer to be used for the copy
	 * @return the number of chars copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static long copy(Reader input, Writer output, final long inputOffset, final long length, char[] buffer)
			throws IOException {
		if (inputOffset > 0) {
			skipFully(input, inputOffset);
		}
		if (length == 0) {
			return 0;
		}
		int bytesToRead = buffer.length;
		if (length > 0 && length < buffer.length) {
			bytesToRead = (int) length;
		}
		int read;
		long totalRead = 0;
		while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
			output.write(buffer, 0, read);
			totalRead += read;
			if (length > 0) { // only adjust length if not reading to the end
				// Note the cast must work because buffer.length is an integer
				bytesToRead = (int) Math.min(length - totalRead, buffer.length);
			}
		}
		return totalRead;
	}

	// content equals
	// -----------------------------------------------------------------------
	/**
	 * Compare the contents of two Streams to determine if they are equal or not.
	 * <p>
	 * This method buffers the input internally using
	 * <code>BufferedInputStream</code> if they are not already buffered.
	 *
	 * @param input1 the first stream
	 * @param input2 the second stream
	 * @return true if the content of the streams are equal or they both don't
	 *         exist, false otherwise
	 * @throws NullPointerException if either input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
		if (!(input1 instanceof BufferedInputStream)) {
			input1 = new BufferedInputStream(input1);
		}
		if (!(input2 instanceof BufferedInputStream)) {
			input2 = new BufferedInputStream(input2);
		}

		int ch = input1.read();
		while (EOF != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}

		int ch2 = input2.read();
		return ch2 == EOF;
	}

	/**
	 * Compare the contents of two Readers to determine if they are equal or not.
	 * <p>
	 * This method buffers the input internally using <code>BufferedReader</code> if
	 * they are not already buffered.
	 *
	 * @param input1 the first reader
	 * @param input2 the second reader
	 * @return true if the content of the readers are equal or they both don't
	 *         exist, false otherwise
	 * @throws NullPointerException if either input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static boolean contentEquals(Reader input1, Reader input2) throws IOException {

		input1 = toBufferedReader(input1);
		input2 = toBufferedReader(input2);

		int ch = input1.read();
		while (EOF != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}

		int ch2 = input2.read();
		return ch2 == EOF;
	}

	/**
	 * Compare the contents of two Readers to determine if they are equal or not,
	 * ignoring EOL characters.
	 * <p>
	 * This method buffers the input internally using <code>BufferedReader</code> if
	 * they are not already buffered.
	 *
	 * @param input1 the first reader
	 * @param input2 the second reader
	 * @return true if the content of the readers are equal (ignoring EOL
	 *         differences), false otherwise
	 * @throws NullPointerException if either input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2) throws IOException {
		BufferedReader br1 = toBufferedReader(input1);
		BufferedReader br2 = toBufferedReader(input2);

		String line1 = br1.readLine();
		String line2 = br2.readLine();
		while (line1 != null && line2 != null && line1.equals(line2)) {
			line1 = br1.readLine();
			line2 = br2.readLine();
		}
		return line1 == null ? line2 == null ? true : false : line1.equals(line2);
	}

	/**
	 * Skip bytes from an input byte stream. This implementation guarantees that it
	 * will read as many bytes as possible before giving up; this may not always be
	 * the case for subclasses of {@link Reader}.
	 * 
	 * @param input  byte stream to skip
	 * @param toSkip number of bytes to skip.
	 * @return number of bytes actually skipped.
	 * 
	 * @see InputStream#skip(long)
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if toSkip is negative
	 */
	public static long skip(InputStream input, long toSkip) throws IOException {
		if (toSkip < 0) {
			throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
		}
		/*
		 * N.B. no need to synchronize this because: - we don't care if the buffer is
		 * created multiple times (the data is ignored) - we always use the same size
		 * buffer, so if it it is recreated it will still be OK (if the buffer size were
		 * variable, we would need to synch. to ensure some other thread did not create
		 * a smaller one)
		 */
		if (SKIP_BYTE_BUFFER == null) {
			SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
		}
		long remain = toSkip;
		while (remain > 0) {
			long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
			if (n < 0) { // EOF
				break;
			}
			remain -= n;
		}
		return toSkip - remain;
	}

	/**
	 * Skip characters from an input character stream. This implementation
	 * guarantees that it will read as many characters as possible before giving up;
	 * this may not always be the case for subclasses of {@link Reader}.
	 * 
	 * @param input  character stream to skip
	 * @param toSkip number of characters to skip.
	 * @return number of characters actually skipped.
	 * 
	 * @see Reader#skip(long)
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if toSkip is negative
	 */
	public static long skip(Reader input, long toSkip) throws IOException {
		if (toSkip < 0) {
			throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
		}
		/*
		 * N.B. no need to synchronize this because: - we don't care if the buffer is
		 * created multiple times (the data is ignored) - we always use the same size
		 * buffer, so if it it is recreated it will still be OK (if the buffer size were
		 * variable, we would need to synch. to ensure some other thread did not create
		 * a smaller one)
		 */
		if (SKIP_CHAR_BUFFER == null) {
			SKIP_CHAR_BUFFER = new char[SKIP_BUFFER_SIZE];
		}
		long remain = toSkip;
		while (remain > 0) {
			long n = input.read(SKIP_CHAR_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
			if (n < 0) { // EOF
				break;
			}
			remain -= n;
		}
		return toSkip - remain;
	}

	/**
	 * Skip the requested number of bytes or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that {@link InputStream#skip(long)} may not
	 * skip as many bytes as requested (most likely because of reaching EOF).
	 * 
	 * @param input  stream to skip
	 * @param toSkip the number of bytes to skip
	 * @see InputStream#skip(long)
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if toSkip is negative
	 * @throws EOFException             if the number of bytes skipped was incorrect
	 */
	public static void skipFully(InputStream input, long toSkip) throws IOException {
		if (toSkip < 0) {
			throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
		}
		long skipped = skip(input, toSkip);
		if (skipped != toSkip) {
			throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
		}
	}

	/**
	 * Skip the requested number of characters or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that {@link Reader#skip(long)} may not skip
	 * as many characters as requested (most likely because of reaching EOF).
	 * 
	 * @param input  stream to skip
	 * @param toSkip the number of characters to skip
	 * @see Reader#skip(long)
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if toSkip is negative
	 * @throws EOFException             if the number of characters skipped was
	 *                                  incorrect
	 */
	public static void skipFully(Reader input, long toSkip) throws IOException {
		long skipped = skip(input, toSkip);
		if (skipped != toSkip) {
			throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
		}
	}

	public static <E extends Throwable> long read(CharBuffer buffer,
			BufferProcessor<? super char[], ? extends E> reader) throws E {
		return read(buffer, DEFAULT_BUFFER_SIZE, reader);
	}

	public static <E extends Throwable> long read(ByteBuffer buffer, BufferProcessor<byte[], E> reader) throws E {
		return read(buffer, DEFAULT_BUFFER_SIZE, reader);
	}

	public static <E extends Throwable> long read(CharBuffer buffer, int bufferSize,
			BufferProcessor<? super char[], ? extends E> reader) throws E {
		Assert.isTrue(bufferSize > 0, "Buffersize needs to be greater than 0");
		if (reader == null) {
			return 0;
		}

		if (buffer == null || !buffer.hasRemaining()) {
			return 0;
		}

		if (buffer.hasArray()) {
			char[] b = buffer.array();
			int ofs = buffer.arrayOffset();
			int pos = buffer.position();
			int lim = buffer.limit();
			reader.process(b, ofs + pos, lim - pos);
			buffer.position(lim);
			return lim - pos;
		} else {
			int len = buffer.remaining();
			int n = Math.min(len, bufferSize);
			char[] tempArray = new char[n];
			long size = 0;
			while (len > 0) {
				int chunk = Math.min(len, tempArray.length);
				buffer.get(tempArray, 0, chunk);
				reader.process(tempArray, 0, chunk);
				len -= chunk;
				size += chunk;
			}
			return size;
		}
	}

	public static <E extends Throwable> long read(ByteBuffer buffer, int bufferSize, BufferProcessor<byte[], E> reader)
			throws E {
		Assert.isTrue(bufferSize > 0, "Buffersize needs to be greater than 0");
		if (reader == null) {
			return 0;
		}

		if (buffer == null || !buffer.hasRemaining()) {
			return 0;
		}

		if (buffer.hasArray()) {
			byte[] b = buffer.array();
			int ofs = buffer.arrayOffset();
			int pos = buffer.position();
			int lim = buffer.limit();
			reader.process(b, ofs + pos, lim - pos);
			buffer.position(lim);
			return lim - pos;
		} else {
			int len = buffer.remaining();
			int n = Math.min(len, bufferSize);
			byte[] tempArray = new byte[n];
			long size = 0;
			while (len > 0) {
				int chunk = Math.min(len, tempArray.length);
				buffer.get(tempArray, 0, chunk);
				reader.process(tempArray, 0, chunk);
				len -= chunk;
				size += chunk;
			}
			return size;
		}
	}

	public static <E extends Throwable> long read(InputStream input, BufferProcessor<byte[], E> reader)
			throws E, IOException {
		return read(input, DEFAULT_BUFFER_SIZE, reader);
	}

	public static <E extends Throwable> long read(InputStream input, int bufferSize, BufferProcessor<byte[], E> reader)
			throws E, IOException {
		Assert.isTrue(bufferSize > 0, "Buffersize needs to be greater than 0");
		if (reader == null || input == null) {
			return 0;
		}

		byte[] buffer = new byte[bufferSize];
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			reader.process(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Read characters from an input character stream. This implementation
	 * guarantees that it will read as many characters as possible before giving up;
	 * this may not always be the case for subclasses of {@link Reader}.
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @param offset inital offset into buffer
	 * @param length length to read, must be &gt;= 0
	 * @return actual length read; may be less than requested if EOF was reached
	 * @throws IOException if a read error occurs
	 */
	public static int read(Reader input, char[] buffer, int offset, int length) throws IOException {
		if (length < 0) {
			throw new IllegalArgumentException("Length must not be negative: " + length);
		}
		int remaining = length;
		while (remaining > 0) {
			int location = length - remaining;
			int count = input.read(buffer, offset + location, remaining);
			if (EOF == count) { // EOF
				break;
			}
			remaining -= count;
		}
		return length - remaining;
	}

	/**
	 * Read characters from an input character stream. This implementation
	 * guarantees that it will read as many characters as possible before giving up;
	 * this may not always be the case for subclasses of {@link Reader}.
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @return actual length read; may be less than requested if EOF was reached
	 * @throws IOException if a read error occurs
	 */
	public static int read(Reader input, char[] buffer) throws IOException {
		return read(input, buffer, 0, buffer.length);
	}

	/**
	 * Read bytes from an input stream. This implementation guarantees that it will
	 * read as many bytes as possible before giving up; this may not always be the
	 * case for subclasses of {@link InputStream}.
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @param offset inital offset into buffer
	 * @param length length to read, must be &gt;= 0
	 * @return actual length read; may be less than requested if EOF was reached
	 * @throws IOException if a read error occurs
	 */
	public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
		if (length < 0) {
			throw new IllegalArgumentException("Length must not be negative: " + length);
		}
		int remaining = length;
		while (remaining > 0) {
			int location = length - remaining;
			int count = input.read(buffer, offset + location, remaining);
			if (EOF == count) { // EOF
				break;
			}
			remaining -= count;
		}
		return length - remaining;
	}

	/**
	 * Read bytes from an input stream. This implementation guarantees that it will
	 * read as many bytes as possible before giving up; this may not always be the
	 * case for subclasses of {@link InputStream}.
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @return actual length read; may be less than requested if EOF was reached
	 * @throws IOException if a read error occurs
	 */
	public static int read(InputStream input, byte[] buffer) throws IOException {
		return read(input, buffer, 0, buffer.length);
	}

	/**
	 * Read the requested number of characters or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that {@link Reader#read(char[], int, int)}
	 * may not read as many characters as requested (most likely because of reaching
	 * EOF).
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @param offset inital offset into buffer
	 * @param length length to read, must be &gt;= 0
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if length is negative
	 * @throws EOFException             if the number of characters read was
	 *                                  incorrect
	 */
	public static void readFully(Reader input, char[] buffer, int offset, int length) throws IOException {
		int actual = read(input, buffer, offset, length);
		if (actual != length) {
			throw new EOFException("Length to read: " + length + " actual: " + actual);
		}
	}

	/**
	 * Read the requested number of characters or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that {@link Reader#read(char[], int, int)}
	 * may not read as many characters as requested (most likely because of reaching
	 * EOF).
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if length is negative
	 * @throws EOFException             if the number of characters read was
	 *                                  incorrect
	 */
	public static void readFully(Reader input, char[] buffer) throws IOException {
		readFully(input, buffer, 0, buffer.length);
	}

	/**
	 * Read the requested number of bytes or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that
	 * {@link InputStream#read(byte[], int, int)} may not read as many bytes as
	 * requested (most likely because of reaching EOF).
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * @param offset inital offset into buffer
	 * @param length length to read, must be &gt;= 0
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if length is negative
	 * @throws EOFException             if the number of bytes read was incorrect
	 */
	public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
		int actual = read(input, buffer, offset, length);
		if (actual != length) {
			throw new EOFException("Length to read: " + length + " actual: " + actual);
		}
	}

	/**
	 * Read the requested number of bytes or fail if there are not enough left.
	 * <p>
	 * This allows for the possibility that
	 * {@link InputStream#read(byte[], int, int)} may not read as many bytes as
	 * requested (most likely because of reaching EOF).
	 * 
	 * @param input  where to read input from
	 * @param buffer destination
	 * 
	 * @throws IOException              if there is a problem reading the file
	 * @throws IllegalArgumentException if length is negative
	 * @throws EOFException             if the number of bytes read was incorrect
	 */
	public static void readFully(InputStream input, byte[] buffer) throws IOException {
		readFully(input, buffer, 0, buffer.length);
	}

	public static String readContent(InputStream inputStream, int buffSize, String charsetName) throws IOException {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(inputStream, charsetName);
			return read(isr, buffSize);
		} finally {
			close(isr);
		}
	}

	public static String readContent(InputStream inputStream, String charsetName) throws IOException {
		return readContent(inputStream, DEFAULT_READ_BUFFER_SIZE, charsetName);
	}

	/**
	 * Copy the contents of the given InputStream into a new byte array. Leaves the
	 * stream open when done.
	 * 
	 * @param in the stream to copy from (may be {@code null} or empty)
	 * @return the new byte array that has been copied to (possibly empty)
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray(InputStream in) throws IOException {
		return copyToByteArray(in, DEFAULT_BUFFER_SIZE);
	}

	public static byte[] copyToByteArray(InputStream in, int bufferSize) throws IOException {
		if (in == null) {
			return new byte[0];
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
		copy(in, out, new byte[bufferSize]);
		return out.toByteArray();
	}

	/**
	 * Copy the contents of the given InputStream into a String. Leaves the stream
	 * open when done.
	 * 
	 * @param in the InputStream to copy from (may be {@code null} or empty)
	 * @return the String that has been copied to (possibly empty)
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString(InputStream in, Charset charset) throws IOException {
		if (in == null) {
			return "";
		}

		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}

	/**
	 * Copy a range of content of the given InputStream to the given OutputStream.
	 * <p>
	 * If the specified range exceeds the length of the InputStream, this copies up
	 * to the end of the stream and returns the actual number of copied bytes.
	 * <p>
	 * Leaves both streams open when done.
	 * 
	 * @param in    the InputStream to copy from
	 * @param out   the OutputStream to copy to
	 * @param start the position to start copying from
	 * @param end   the position to end copying
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static long copyRange(@NonNull InputStream in, @NonNull OutputStream out, long start, long end)
			throws IOException {
		long skipped = in.skip(start);
		if (skipped < start) {
			throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required");
		}

		long bytesToCopy = end - start + 1;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while (bytesToCopy > 0) {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1) {
				break;
			} else if (bytesRead <= bytesToCopy) {
				out.write(buffer, 0, bytesRead);
				bytesToCopy -= bytesRead;
			} else {
				out.write(buffer, 0, (int) bytesToCopy);
				bytesToCopy = 0;
			}
		}
		return (end - start + 1 - bytesToCopy);
	}

	/**
	 * Drain the remaining content of the given InputStream. Leaves the InputStream
	 * open when done.
	 * 
	 * @param in the InputStream to drain
	 * @return the number of bytes read
	 * @throws IOException in case of I/O errors
	 */
	public static long drain(@NonNull InputStream in) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int bytesRead = -1;
		long byteCount = 0;
		while ((bytesRead = in.read(buffer)) != -1) {
			byteCount += bytesRead;
		}
		return byteCount;
	}

	/**
	 * Return an efficient empty {@link InputStream}.
	 * 
	 * @return a {@link ByteArrayInputStream} based on an empty byte array
	 */
	public static InputStream emptyInput() {
		return new ByteArrayInputStream(EMPTY_CONTENT);
	}

	/**
	 * 包装成一个忽略close方法的流
	 * 
	 * @param in 输入
	 * @return
	 */
	public static InputStream nonClosing(@NonNull InputStream in) {
		return new NonClosingInputStream(in);
	}

	/**
	 * 包装成一个忽略close方法的流
	 * 
	 * @param out
	 * @return
	 */
	public static OutputStream nonClosing(@NonNull OutputStream out) {
		return new NonClosingOutputStream(out);
	}

	/**
	 * 分割读取
	 * 
	 * @param source
	 * @param separator
	 * @return
	 */
	public static Stream<CharSequence> split(Readable source, String separator) {
		return split(source, CharBuffer.allocate(DEFAULT_BUFFER_SIZE), separator);
	}

	/**
	 * 分割读取
	 * 
	 * @param source
	 * @param buffer
	 * @param separator
	 * @return
	 */
	public static Stream<CharSequence> split(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull CharSequence separator) {
		SplitReadableIterator iterator = new SplitReadableIterator(source, buffer, separator);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	private static class NonClosingInputStream extends FilterInputStream {

		public NonClosingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public void close() throws IOException {
		}
	}

	private static class NonClosingOutputStream extends FilterOutputStream {

		public NonClosingOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(byte[] b, int off, int let) throws IOException {
			// It is critical that we override this method for performance
			out.write(b, off, let);
		}

		@Override
		public void close() throws IOException {
		}
	}
}
