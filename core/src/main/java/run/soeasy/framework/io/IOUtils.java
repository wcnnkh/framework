package run.soeasy.framework.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;

@UtilityClass
public final class IOUtils {
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	private final static InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);

	private static final int EOF = -1;

	public static long transferTo(Reader input, Appendable output) throws IOException {
		return transferTo(input, output, DEFAULT_BUFFER_SIZE);
	}

	public static long transferTo(Reader input, Appendable output, int bufferSize) throws IOException {
		return transferTo(input, output, new char[bufferSize]);
	}

	public static long transferTo(Reader input, Appendable output, char[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.append(new String(buffer, 0, n));
			count += n;
		}
		return count;
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
	public static long transferTo(InputStream input, OutputStream output) throws IOException {
		return transferTo(input, output, DEFAULT_BUFFER_SIZE);
	}

	public static long transferTo(InputStream input, OutputStream output, int bufferSize) throws IOException {
		return transferTo(input, output, new byte[bufferSize]);
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
	public static long transferTo(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static long transferTo(Reader input, Writer output) throws IOException {
		return transferTo(input, output, DEFAULT_BUFFER_SIZE);
	}

	public static long transferTo(Reader input, Writer output, int bufferSize) throws IOException {
		return transferTo(input, output, new char[bufferSize]);
	}

	public static long transferTo(Reader input, Writer output, char[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 一个空的输入
	 * 
	 * @return 返回一个单例
	 */
	public static InputStream emptyInput() {
		return EMPTY_INPUT_STREAM;
	}

	/**
	 * 一个空的输出
	 * 
	 * @return 返回一个单例
	 */
	public static OutputStream nullOutput() {
		return NullOutputStream.NULL_OUTPUT_STREAM;
	}

	public static <E extends Throwable> long transferTo(InputStream input,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_BUFFER_SIZE, bufferConsumer);
	}

	public static <E extends Throwable> long transferTo(InputStream input, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, bufferSize, bufferConsumer);
	}

	public static <E extends Throwable> long transferTo(@NonNull InputStream input, byte[] buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			bufferConsumer.accept(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static Stream<String> readLines(BufferedReader bufferedReader) {
		LineIterator iterator = new LineIterator(bufferedReader);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	public static Stream<String> readLines(InputStream input) {
		InputStreamReader reader = new InputStreamReader(input);
		return readLines(reader).onClose(() -> closeQuietly(reader));
	}

	public static Stream<String> readLines(@NonNull InputStream input, String encoding)
			throws UnsupportedEncodingException {
		if (encoding == null) {
			return readLines(input);
		} else {
			InputStreamReader reader = new InputStreamReader(input, encoding);
			return readLines(reader).onClose(() -> closeQuietly(reader));
		}
	}

	public static Stream<String> readLines(Reader reader) {
		if (reader instanceof BufferedReader) {
			return readLines((BufferedReader) reader);
		}

		BufferedReader bufferedReader = new BufferedReader(reader);
		return readLines(bufferedReader).onClose(() -> closeQuietly(bufferedReader));
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
		return toByteArray(input, DEFAULT_BUFFER_SIZE);
	}

	public static byte[] toByteArray(InputStream input, int bufferSize) throws IOException {
		return toByteArray(input, new byte[bufferSize]);
	}

	public static byte[] toByteArray(InputStream input, byte[] buffer) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transferTo(input, output, buffer);
		return output.toByteArray();
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
		transferTo(input, sw);
		return sw.toCharArray();
	}

	public static CharSequence toCharSequence(@NonNull Reader reader) throws IOException {
		StringWriter writer = new StringWriter();
		transferTo(reader, writer);
		return writer.getBuffer();
	}
}
