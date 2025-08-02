package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 可关闭的输入流读取器，扩展自{@link InputStreamReader}，
 * 确保底层{@link InputStream}和读取器本身的资源被正确释放。
 * 
 * <p>该类重写了{@link #close()}方法，
 * 保证在关闭操作完成后强制关闭底层输入流，避免资源泄漏。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>资源安全：确保输入流和读取器的close操作按顺序执行</li>
 *   <li>异常处理：在finally块中处理资源关闭，保证可靠性</li>
 *   <li>编码支持：完全继承父类的字符集和编码转换功能</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>需要严格管理输入流资源的场景（如网络连接、文件操作）</li>
 *   <li>希望确保底层流与读取器同步关闭的场景</li>
 *   <li>需要确保数据读取完整性的场景</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see InputStreamReader
 * @see InputStream
 */
public class CloseableInputStreamReader extends InputStreamReader {
    private final InputStream in;

    /**
     * 使用指定字符集名称构造读取器。
     * 
     * @param in           底层输入流，不可为null
     * @param charsetName  字符集名称，不可为null
     * @throws UnsupportedEncodingException 当指定的字符集不支持时抛出
     */
    public CloseableInputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in, charsetName);
        this.in = in;
    }

    /**
     * 使用指定字符集解码器构造读取器。
     * 
     * @param in  底层输入流，不可为null
     * @param dec 字符集解码器，不可为null
     */
    public CloseableInputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in, dec);
        this.in = in;
    }

    /**
     * 使用指定字符集构造读取器。
     * 
     * @param in  底层输入流，不可为null
     * @param cs  字符集，不可为null
     */
    public CloseableInputStreamReader(InputStream in, Charset cs) {
        super(in, cs);
        this.in = in;
    }

    /**
     * 使用默认字符集构造读取器。
     * 
     * @param in  底层输入流，不可为null
     */
    public CloseableInputStreamReader(InputStream in) {
        super(in);
        this.in = in;
    }

    /**
     * 关闭此读取器并关闭底层输入流。
     * <p>
     * 先调用父类的close方法关闭读取器，
     * 再在finally块中关闭底层输入流，确保资源被正确释放。
     * 
     * @throws IOException 当关闭过程中发生I/O错误时抛出
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            in.close();
        }
    }
}