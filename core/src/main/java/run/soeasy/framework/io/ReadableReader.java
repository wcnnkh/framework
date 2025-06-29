package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * 将 Readable 包装为 Reader 的工具类 实现了将任何实现了 Readable 接口的对象转换为 Reader
 */
public class ReadableReader extends Reader {

	private final Readable readable;
	private boolean closed = false;

	/**
	 * 构造函数，接收一个 Readable 实例
	 * 
	 * @param readable 要包装的 Readable 对象
	 * @throws NullPointerException 如果 readable 为 null
	 */
	public ReadableReader(Readable readable) {
		if (readable == null) {
			throw new NullPointerException("Readable cannot be null");
		}
		this.readable = readable;
	}

	/**
	 * 读取单个字符
	 * 
	 * @return 读取的字符，或者 -1 表示已到达流的末尾
	 * @throws IOException           如果发生 I/O 错误
	 * @throws IllegalStateException 如果 Reader 已关闭
	 */
	@Override
	public int read() throws IOException {
		ensureOpen();
		CharBuffer cb = CharBuffer.allocate(1);
		int charsRead = readable.read(cb);
		if (charsRead == -1) {
			return -1;
		}
		cb.flip();
		return cb.get();
	}

	/**
	 * 读取字符到字符数组的一部分
	 * 
	 * @param cbuf 目标字符数组
	 * @param off  数组起始偏移量
	 * @param len  要读取的最大字符数
	 * @return 实际读取的字符数，或者 -1 表示已到达流的末尾
	 * @throws IOException           如果发生 I/O 错误
	 * @throws IllegalStateException 如果 Reader 已关闭
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		ensureOpen();
		if (cbuf == null) {
			throw new NullPointerException("Character buffer cannot be null");
		}
		if (off < 0 || len < 0 || off > cbuf.length - len) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}

		CharBuffer cb = CharBuffer.wrap(cbuf, off, len);
		int charsRead = readable.read(cb);
		if (charsRead == -1) {
			return -1;
		}
		return charsRead;
	}

	/**
	 * 关闭 Reader
	 * 
	 * @throws IOException 如果发生 I/O 错误
	 */
	@Override
	public void close() throws IOException {
		if (!closed) {
			closed = true;
			// 如果 Readable 实现了 AutoCloseable，也关闭它
			if (readable instanceof AutoCloseable) {
				try {
					((AutoCloseable) readable).close();
				} catch (Exception e) {
					if (e instanceof IOException) {
						throw (IOException) e;
					} else {
						throw new IOException("Error closing Readable", e);
					}
				}
			}
		}
	}

	/**
	 * 确保 Reader 处于打开状态
	 * 
	 * @throws IOException 如果 Reader 已关闭
	 */
	private void ensureOpen() throws IOException {
		if (closed) {
			throw new IOException("Reader is closed");
		}
	}
}