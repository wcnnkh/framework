package io.basc.framework.io;

import io.basc.framework.util.AbstractIterator;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

public class LineIterator extends AbstractIterator<String> implements Closeable {

    // N.B. This class deliberately does not implement Iterable, see https://issues.apache.org/jira/browse/IO-181

    /** The reader that is being read. */
    private final BufferedReader bufferedReader;
    /** The current line. */
    private String cachedLine;
    /** A flag indicating if the iterator has been fully read. */
    private boolean finished = false;

    /**
     * Constructs an iterator of the lines for a <code>Reader</code>.
     *
     * @param reader the <code>Reader</code> to read from, not null
     * @throws IllegalArgumentException if the reader is null
     */
    public LineIterator(final Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Indicates whether the <code>Reader</code> has more lines.
     * If there is an <code>IOException</code> then {@link #close()} will
     * be called on this instance.
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
            } catch(final IOException ioe) {
                IOUtils.closeQuietly(this, e -> ioe.addSuppressed(e));
                throw new IllegalStateException(ioe);
            }
        }
    }

    /**
     * Overridable method to validate each line that is returned.
     * This implementation always returns true.
     * @param line  the line that is to be validated
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

    /**
     * Closes the underlying {@code Reader}.
     * This method is useful if you only want to process the first few
     * lines of a larger file. If you do not close the iterator
     * then the {@code Reader} remains open.
     * This method can safely be called multiple times.
     *
     * @throws IOException if closing the underlying {@code Reader} fails.
     */
    @Override
    public void close() throws IOException {
        finished = true;
        cachedLine = null;
        IOUtils.close(bufferedReader);
    }

    /**
     * Unsupported.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported on LineIterator");
    }

    //-----------------------------------------------------------------------
    /**
     * Closes a {@code LineIterator} quietly.
     *
     * @param iterator The iterator to close, or {@code null}.
     * @deprecated As of 2.6 deprecated without replacement. Please use the try-with-resources statement or handle
     * suppressed exceptions manually.
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    @Deprecated
    public static void closeQuietly(final LineIterator iterator) {
        IOUtils.closeQuietly(iterator);
    }

}
