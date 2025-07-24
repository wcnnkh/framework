package run.soeasy.framework.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 基于{@link BufferedReader}的行迭代器，用于逐行读取文本内容。
 * 该迭代器实现了{@link Iterator}接口，支持使用标准迭代器模式遍历文本行。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>懒加载：仅在需要时读取下一行，避免一次性加载整个文件</li>
 *   <li>过滤机制：通过重写{@link #isValidLine(String)}方法可自定义过滤逻辑</li>
 *   <li>异常处理：自动处理IO异常并转换为运行时异常</li>
 *   <li>资源安全：迭代结束后可通过关闭Reader释放资源</li>
 * </ul>
 * 
 * <p><b>使用注意事项：</b>
 * <ul>
 *   <li>迭代过程中请勿手动操作底层Reader，否则可能导致迭代异常</li>
 *   <li>迭代结束后需手动关闭Reader，建议使用try-with-resources结构</li>
 *   <li>非线程安全，不适合多线程环境下使用</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Iterator
 * @see BufferedReader
 */
class LineIterator implements Iterator<String> {
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
     * <code>IOException</code> then {@link BufferedReader#close()} will be called
     * on this instance.
     *
     * @return {@code true} if the Reader has more valid lines
     * @throws IllegalStateException if an IO exception occurs during reading
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
                // 转换检查异常为运行时异常，保持接口一致性
                throw new IllegalStateException("IO error while reading next line", ioe);
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
     * @return the next valid line from the input
     * @throws NoSuchElementException if there is no more line to return
     */
    @Override
    public String next() {
        return nextLine();
    }

    /**
     * Returns the next line in the wrapped <code>Reader</code>.
     *
     * @return the next valid line from the input
     * @throws NoSuchElementException if there is no more line to return
     */
    public String nextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines available in reader");
        }
        final String currentLine = cachedLine;
        cachedLine = null;
        return currentLine;
    }
}