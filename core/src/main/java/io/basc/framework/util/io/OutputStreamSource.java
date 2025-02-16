package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface OutputStreamSource<T extends OutputStream> {

	public static interface CharsetOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends EncodeOutputStreamSource<T, Writer, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Writer, ? extends IOException> getEncoder() {
			return (e) -> new OutputStreamWriter(e, getCharset());
		}
	}

	public static class DefaultEncodeOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends StandardEncodeOutputStreamSource<T, Writer, W> {

		public DefaultEncodeOutputStreamSource(@NonNull W source) {
			super(source, OutputStreamWriter::new);
		}

		public DefaultEncodeOutputStreamSource(@NonNull W source, @NonNull CharsetEncoder charsetEncoder) {
			super(source, (e) -> new OutputStreamWriter(e, charsetEncoder));
		}
	}

	public static interface EncodeOutputStreamSource<T extends OutputStream, R extends Writer, W extends OutputStreamSource<T>>
			extends OutputStreamSourceWrapper<T, W>, WriterSource<R> {
		Function<? super T, ? extends R, ? extends IOException> getEncoder();

		@Override
		default @NonNull Pipeline<R, IOException> getWriter() {
			return getSource().getOutputStream().map(getEncoder());
		}
	}

	@FunctionalInterface
	public static interface OutputStreamSourceWrapper<T extends OutputStream, W extends OutputStreamSource<T>>
			extends OutputStreamSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getOutputStream() {
			return getSource().getOutputStream();
		}

		@Override
		default <R extends Writer> WriterSource<R> toWriterSource(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().toWriterSource(pipeline);
		}

		@Override
		default WriterSource<Writer> toWriterSource() {
			return getSource().toWriterSource();
		}

		@Override
		default WriterSource<Writer> toWriterSource(Charset charset) {
			return getSource().toWriterSource(charset);
		}

		@Override
		default WriterSource<Writer> toWriterSource(CharsetEncoder charsetEncoder) {
			return getSource().toWriterSource(charsetEncoder);
		}

		@Override
		default WriterSource<Writer> toWriterSource(String charsetName) {
			return getSource().toWriterSource(charsetName);
		}

		@Override
		default void write(@NonNull byte[] b) throws IOException {
			getSource().write(b);
		}

		@Override
		default void write(@NonNull byte[] b, int off, int len) throws IOException {
			getSource().write(b, off, len);
		}

		@Override
		default int write(@NonNull ByteBuffer buffer) throws IOException {
			return getSource().write(buffer);
		}

		@Override
		default <S extends InputStream> void write(@NonNull InputStreamSource<? extends S> source) throws IOException {
			getSource().write(source);
		}
	}

	public static class StandardCharsetOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends StandardEncodeOutputStreamSource<T, Writer, W> implements CharsetOutputStreamSource<T, W> {
		private final Object charset;

		public StandardCharsetOutputStreamSource(@NonNull W source, Charset charset) {
			super(source, (e) -> new OutputStreamWriter(e, charset));
			this.charset = charset;
		}

		public StandardCharsetOutputStreamSource(@NonNull W source, String charsetName) {
			super(source, (e) -> new OutputStreamWriter(e, charsetName));
			this.charset = charsetName;
		}

		@Override
		public Charset getCharset() {
			if (charset instanceof Charset) {
				return (Charset) charset;
			}
			return Charset.forName(String.valueOf(charset));
		}

		@Override
		public String getCharsetName() {

			if (charset instanceof String) {
				return (String) charset;
			}
			return CharsetOutputStreamSource.super.getCharsetName();
		}

	}

	@Data
	public static class StandardEncodeOutputStreamSource<T extends OutputStream, R extends Writer, W extends OutputStreamSource<T>>
			implements EncodeOutputStreamSource<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> encoder;
	}

	@NonNull
	Pipeline<T, IOException> getOutputStream();

	/**
	 * Writes <code>b.length</code> bytes from the specified byte array to this
	 * output stream. The general contract for <code>write(b)</code> is that it
	 * should have exactly the same effect as the call
	 * <code>write(b, 0, b.length)</code>.
	 *
	 * @param b the data.
	 * @exception IOException if an I/O error occurs.
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	default void write(@NonNull byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this output stream. The general contract for
	 * <code>write(b, off, len)</code> is that some of the bytes in the array
	 * <code>b</code> are written to the output stream in order; element
	 * <code>b[off]</code> is the first byte written and <code>b[off+len-1]</code>
	 * is the last byte written by this operation.
	 * <p>
	 * The <code>write</code> method of <code>OutputStream</code> calls the write
	 * method of one argument on each of the bytes to be written out. Subclasses are
	 * encouraged to override this method and provide a more efficient
	 * implementation.
	 * <p>
	 * If <code>b</code> is <code>null</code>, a <code>NullPointerException</code>
	 * is thrown.
	 * <p>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array <code>b</code>,
	 * then an <tt>IndexOutOfBoundsException</tt> is thrown.
	 *
	 * @param b   the data.
	 * @param off the start offset in the data.
	 * @param len the number of bytes to write.
	 * @exception IOException if an I/O error occurs. In particular, an
	 *                        <code>IOException</code> is thrown if the output
	 *                        stream is closed.
	 */
	default void write(@NonNull byte b[], int off, int len) throws IOException {
		getOutputStream().option().ifPresent((os) -> os.write(b, off, len));
	}

	default int write(@NonNull ByteBuffer buffer) throws IOException {
		// TODO
		return 0;
	}

	default <R extends Writer> WriterSource<R> toWriterSource(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardEncodeOutputStreamSource<>(this, pipeline);
	}

	default WriterSource<Writer> toWriterSource() {
		return new DefaultEncodeOutputStreamSource<>(this);
	}

	default WriterSource<Writer> toWriterSource(Charset charset) {
		return new StandardCharsetOutputStreamSource<>(this, charset);
	}

	default WriterSource<Writer> toWriterSource(CharsetEncoder charsetEncoder) {
		return new DefaultEncodeOutputStreamSource<>(this, charsetEncoder);
	}

	default WriterSource<Writer> toWriterSource(String charsetName) {
		return new StandardCharsetOutputStreamSource<>(this, charsetName);
	}

	default <S extends InputStream> void write(@NonNull InputStreamSource<? extends S> source) throws IOException {
		source.getInputStream().option()
				.ifPresent((is) -> getOutputStream().option().ifPresent((os) -> IOUtils.copy(is, os)));
	}
}
