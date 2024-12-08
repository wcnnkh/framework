package io.basc.framework.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineIterator implements Iterator<String> {
	/** The reader that is being read. */
	private final BufferedReader bufferedReader;
	/** The current line. */
	private String cachedLine;
	/** A flag indicating if the iterator has been fully read. */
	private boolean finished = false;

	/**
	 * Constructs an iterator of the lines for a <code>Reader</code>.
	 *
	 * @param bufferedReader the <code>Reader</code> to read from, not null
	 * @throws IllegalArgumentException if the reader is null
	 */
	public LineIterator(final BufferedReader bufferedReader) throws IllegalArgumentException {
		if (bufferedReader == null) {
			throw new IllegalArgumentException("Reader must not be null");
		}
		this.bufferedReader = bufferedReader;
	}

	// -----------------------------------------------------------------------
	/**
	 * Indicates whether the <code>Reader</code> has more lines. If there is an
	 * <code>IOException</code> then {@link BufferedReader#close()} will be called on this
	 * instance.
	 *
	 * @return {@code true} if the Reader has more lines
	 * @throws IllegalStateException if an IO exception occurs
	 */
	@Override
	public boolean hasNext() {
		if (cachedLine != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			try {
				while (true) {
					final String line = bufferedReader.readLine();
					if (line == null) {
						finished = true;
						return false;
					} else if (isValidLine(line)) {
						cachedLine = line;
						return true;
					}
				}
			} catch (final IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}
	}

	/**
	 * Overridable method to validate each line that is returned. This
	 * implementation always returns true.
	 * 
	 * @param line the line that is to be validated
	 * @return true if valid, false to remove from the iterator
	 */
	protected boolean isValidLine(final String line) {
		return true;
	}

	/**
	 * Returns the next line in the wrapped <code>Reader</code>.
	 *
	 * @return the next line from the input
	 * @throws NoSuchElementException if there is no line to return
	 */
	@Override
	public String next() {
		return nextLine();
	}

	/**
	 * Returns the next line in the wrapped <code>Reader</code>.
	 *
	 * @return the next line from the input
	 * @throws NoSuchElementException if there is no line to return
	 */
	public String nextLine() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more lines");
		}
		final String currentLine = cachedLine;
		cachedLine = null;
		return currentLine;
	}
}
