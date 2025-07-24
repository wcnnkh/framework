package run.soeasy.framework.io;

import java.io.InputStream;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 基于{@link ByteBuffer}的输入流实现，将ByteBuffer的读取操作适配为标准InputStream接口，
 * 支持从ByteBuffer中读取字节数据，适用于需要在NIO ByteBuffer和标准IO InputStream之间转换的场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>字节缓冲读取：封装ByteBuffer的remaining()/get()操作</li>
 *   <li>标准接口适配：实现InputStream的read()/available()等方法</li>
 *   <li>线程安全：仅支持单线程读取（ByteBuffer本身非线程安全）</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>NIO与标准IO转换：将ByteBuffer数据传递给需要InputStream的API</li>
 *   <li>内存数据读取：从堆外内存(DirectByteBuffer)读取数据</li>
 *   <li>分段数据处理：配合ByteBuffer的mark/reset实现重读功能</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see ByteBuffer
 * @see InputStream
 */
@Getter
@RequiredArgsConstructor
public class ByteBufferInputStream extends InputStream {
    @NonNull
    private final ByteBuffer buffer;

    /**
     * 返回当前可读取的字节数（基于ByteBuffer的remaining()方法）。
     * 
     * @return ByteBuffer中剩余的字节数
     */
    @Override
    public int available() {
        return buffer.remaining();
    }

    /**
     * 读取单个字节（从ByteBuffer中获取下一个字节）。
     * <p>
     * 执行逻辑：
     * <ol>
     *   <li>检查ByteBuffer是否有剩余字节</li>
     *   <li>若无剩余则返回-1，否则获取字节并转换为无符号整数</li>
     * </ol>
     * 
     * @return 读取的字节（0-255），若无数据则返回-1
     */
    @Override
    public int read() {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    /**
     * 读取字节到数组（从ByteBuffer中批量获取字节）。
     * <p>
     * 执行逻辑：
     * <ol>
     *   <li>检查ByteBuffer是否有剩余字节</li>
     *   <li>计算实际可读取的字节数（不超过请求长度和剩余字节数）</li>
     *   <li>使用ByteBuffer.get(byte[], int, int)读取数据</li>
     *   <li>返回实际读取的字节数</li>
     * </ol>
     * 
     * @param bytes 目标字节数组
     * @param off   数组写入偏移量
     * @param len   请求读取的字节数
     * @return 实际读取的字节数，无数据时返回-1
     * @throws IndexOutOfBoundsException 若off/len参数越界
     */
    @Override
    public int read(byte[] bytes, int off, int len) {
        if (!buffer.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buffer.remaining());
        buffer.get(bytes, off, len);
        return len;
    }
}