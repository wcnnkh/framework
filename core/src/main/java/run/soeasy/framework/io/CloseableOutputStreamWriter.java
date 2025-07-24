package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * 可关闭的输出流写入器，扩展自{@link OutputStreamWriter}，
 * 确保底层{@link OutputStream}和写入器本身的资源被正确释放。
 * 
 * <p>该类重写了{@link #flush()}和{@link #close()}方法，
 * 保证在操作完成后强制刷新并关闭底层输出流，避免资源泄漏。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>资源安全：确保输出流和写入器的close操作按顺序执行</li>
 *   <li>异常处理：在finally块中处理资源关闭，保证可靠性</li>
 *   <li>编码支持：完全继承父类的字符集和编码转换功能</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>需要严格管理输出流资源的场景（如网络连接、文件操作）</li>
 *   <li>希望确保底层流与写入器同步关闭的场景</li>
 *   <li>需要强制刷新输出缓冲区的场景</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see OutputStreamWriter
 * @see OutputStream
 */
public class CloseableOutputStreamWriter extends OutputStreamWriter {
    private final OutputStream out;

    /**
     * 使用默认字符集构造写入器。
     * 
     * @param out 底层输出流，不可为null
     */
    public CloseableOutputStreamWriter(OutputStream out) {
        super(out);
        this.out = out;
    }

    /**
     * 使用指定字符集构造写入器。
     * 
     * @param out  底层输出流，不可为null
     * @param cs   字符集，不可为null
     */
    public CloseableOutputStreamWriter(OutputStream out, Charset cs) {
        super(out, cs);
        this.out = out;
    }

    /**
     * 使用指定字符集编码器构造写入器。
     * 
     * @param out  底层输出流，不可为null
     * @param enc  字符集编码器，不可为null
     */
    public CloseableOutputStreamWriter(OutputStream out, CharsetEncoder enc) {
        super(out, enc);
        this.out = out;
    }

    /**
     * 使用指定字符集名称构造写入器。
     * 
     * @param out          底层输出流，不可为null
     * @param charsetName  字符集名称，不可为null
     * @throws UnsupportedEncodingException 当指定的字符集不支持时抛出
     */
    public CloseableOutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
        super(out, charsetName);
        this.out = out;
    }

    /**
     * 刷新此写入器的缓冲区，并强制刷新底层输出流。
     * <p>
     * 先调用父类的flush方法刷新写入器缓冲区，
     * 再在finally块中刷新底层输出流，确保数据完全写出。
     * 
     * @throws IOException 当刷新过程中发生I/O错误时抛出
     */
    @Override
    public void flush() throws IOException {
        try {
            super.flush();
        } finally {
            out.flush();
        }
    }

    /**
     * 关闭此写入器并关闭底层输出流。
     * <p>
     * 先调用父类的close方法关闭写入器，
     * 再在finally块中关闭底层输出流，确保资源被正确释放。
     * 
     * @throws IOException 当关闭过程中发生I/O错误时抛出
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            out.close();
        }
    }
}