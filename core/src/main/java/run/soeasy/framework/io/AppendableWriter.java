package run.soeasy.framework.io;
import java.io.IOException;
import java.io.Writer;

/**
 * 将 Appendable 包装为 Writer 的工具类
 * 实现了将任何实现了 Appendable 接口的对象转换为 Writer
 */
public class AppendableWriter extends Writer {
    
    private final Appendable appendable;
    private boolean closed = false;
    
    /**
     * 构造函数，接收一个 Appendable 实例
     * @param appendable 要包装的 Appendable 对象
     * @throws NullPointerException 如果 appendable 为 null
     */
    public AppendableWriter(Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("Appendable cannot be null");
        }
        this.appendable = appendable;
    }
    
    /**
     * 写入单个字符
     * @param c 要写入的字符
     * @throws IOException 如果发生 I/O 错误
     * @throws IllegalStateException 如果 Writer 已关闭
     */
    @Override
    public void write(int c) throws IOException {
        ensureOpen();
        try {
            appendable.append((char) c);
        } catch (Exception e) {
            throw new IOException("Error appending character", e);
        }
    }
    
    /**
     * 写入字符数组的一部分
     * @param cbuf 字符数组
     * @param off 数组起始偏移量
     * @param len 要写入的字符数
     * @throws IOException 如果发生 I/O 错误
     * @throws IllegalStateException 如果 Writer 已关闭
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ensureOpen();
        if (cbuf == null) {
            throw new NullPointerException("Character buffer cannot be null");
        }
        if (off < 0 || len < 0 || off > cbuf.length - len) {
            throw new IndexOutOfBoundsException();
        }
        try {
            if (len > 0) {
                appendable.append(new String(cbuf, off, len));
            }
        } catch (Exception e) {
            throw new IOException("Error appending character array", e);
        }
    }
    
    /**
     * 写入字符串的一部分
     * @param str 字符串
     * @param off 字符串起始偏移量
     * @param len 要写入的字符数
     * @throws IOException 如果发生 I/O 错误
     * @throws IllegalStateException 如果 Writer 已关闭
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        ensureOpen();
        if (str == null) {
            throw new NullPointerException("String cannot be null");
        }
        try {
            appendable.append(str, off, off + len);
        } catch (Exception e) {
            throw new IOException("Error appending string", e);
        }
    }
    
    /**
     * 刷新输出流
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IOException("Writer is closed");
        }
        if (appendable instanceof java.io.Flushable) {
            ((java.io.Flushable) appendable).flush();
        }
    }
    
    /**
     * 关闭输出流
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                flush();
            } finally {
                closed = true;
            }
        }
    }
    
    /**
     * 确保 Writer 处于打开状态
     * @throws IOException 如果 Writer 已关闭
     */
    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Writer is closed");
        }
    }
}