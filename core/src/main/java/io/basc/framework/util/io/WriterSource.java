package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface WriterSource<T extends Writer> {
	@FunctionalInterface
	public static interface WriterSourceWrapper<T extends Writer, W extends WriterSource<T>>
			extends WriterSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getWriter() {
			return getSource().getWriter();
		}

		@Override
		default void write(@NonNull char[] cbuf) throws IOException {
			getSource().write(cbuf);
		}

		@Override
		default void write(@NonNull char[] cbuf, int off, int len) throws IOException {
			getSource().write(cbuf, off, len);
		}

		@Override
		default int write(@NonNull CharBuffer buffer) throws IOException {
			return getSource().write(buffer);
		}

		@Override
		default <S extends Reader> void write(@NonNull ReaderSource<? extends S> source) throws IOException {
			getSource().write(source);
		}

		@Override
		default WriterSource<T> append(@NonNull CharSequence csq) throws IOException {
			getSource().append(csq);
			return this;
		}

		@Override
		default WriterSource<T> append(@NonNull CharSequence csq, int start, int end) throws IOException {
			getSource().append(csq, start, end);
			return this;
		}

		@Override
		default <S extends Reader> WriterSource<T> append(@NonNull ReaderSource<? extends S> source)
				throws IOException {
			getSource().append(source);
			return this;
		}

	}

	@NonNull
	Pipeline<T, IOException> getWriter();

	/**
	 * Writes an array of characters.
	 *
	 * @param cbuf Array of characters to be written
	 *
	 * @throws IOException If an I/O error occurs
	 */
	default void write(@NonNull char cbuf[]) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	/**
	 * Writes a portion of an array of characters.
	 *
	 * @param cbuf Array of characters
	 *
	 * @param off  Offset from which to start writing characters
	 *
	 * @param len  Number of characters to write
	 *
	 * @throws IOException If an I/O error occurs
	 */
	default void write(@NonNull char cbuf[], int off, int len) throws IOException {
		getWriter().option().ifPresent((w) -> w.write(cbuf, off, len));
	}

	default int write(@NonNull CharBuffer buffer) throws IOException {
		// TODO
		return 0;
	}

	/**
	 * Appends the specified character sequence to this writer.
	 *
	 * <p>
	 * An invocation of this method of the form <tt>out.append(csq)</tt> behaves in
	 * exactly the same way as the invocation
	 *
	 * <pre>
	 * out.write(csq.toString())
	 * </pre>
	 *
	 * <p>
	 * Depending on the specification of <tt>toString</tt> for the character
	 * sequence <tt>csq</tt>, the entire sequence may not be appended. For instance,
	 * invoking the <tt>toString</tt> method of a character buffer will return a
	 * subsequence whose content depends upon the buffer's position and limit.
	 *
	 * @param csq The character sequence to append. If <tt>csq</tt> is
	 *            <tt>null</tt>, then the four characters <tt>"null"</tt> are
	 *            appended to this writer.
	 *
	 * @return This writer
	 *
	 * @throws IOException If an I/O error occurs
	 *
	 */
	default WriterSource<T> append(@NonNull CharSequence csq) throws IOException {
		return append(csq, 0, csq.length());
	}

	/**
	 * Appends a subsequence of the specified character sequence to this writer.
	 * <tt>Appendable</tt>.
	 *
	 * <p>
	 * An invocation of this method of the form <tt>out.append(csq, start,
	 * end)</tt> when <tt>csq</tt> is not <tt>null</tt> behaves in exactly the same
	 * way as the invocation
	 *
	 * <pre>
	 * out.write(csq.subSequence(start, end).toString())
	 * </pre>
	 *
	 * @param csq   The character sequence from which a subsequence will be
	 *              appended. If <tt>csq</tt> is <tt>null</tt>, then characters will
	 *              be appended as if <tt>csq</tt> contained the four characters
	 *              <tt>"null"</tt>.
	 *
	 * @param start The index of the first character in the subsequence
	 *
	 * @param end   The index of the character following the last character in the
	 *              subsequence
	 *
	 * @return This writer
	 *
	 * @throws IndexOutOfBoundsException If <tt>start</tt> or <tt>end</tt> are
	 *                                   negative, <tt>start</tt> is greater than
	 *                                   <tt>end</tt>, or <tt>end</tt> is greater
	 *                                   than <tt>csq.length()</tt>
	 *
	 * @throws IOException               If an I/O error occurs
	 *
	 */
	default WriterSource<T> append(@NonNull CharSequence csq, int start, int end) throws IOException {
		getWriter().option().ifPresent((w) -> w.append(csq, start, end));
		return this;
	}

	default <S extends Reader> void write(@NonNull ReaderSource<? extends S> source) throws IOException {
		source.getReader().option().ifPresent((r) -> getWriter().option().ifPresent((w) -> IOUtils.copy(r, w)));
	}

	/**
	 * Appends a reader of the specified character sequence to this writer.
	 * 
	 * @param <S>
	 * @param source
	 * @return This writer
	 * @throws IOException
	 */
	default <S extends Reader> WriterSource<T> append(@NonNull ReaderSource<? extends S> source) throws IOException {
		source.getReader().option().ifPresent((r) -> getWriter().option().ifPresent((w) -> IOUtils.append(r, w)));
		return this;
	}
}
