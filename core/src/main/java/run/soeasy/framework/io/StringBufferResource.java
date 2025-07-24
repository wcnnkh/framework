package run.soeasy.framework.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import lombok.Getter;

/**
 * 字符串缓冲区资源实现，基于{@link StringWriter}实现内存字符串资源，
 * 实现{@link Resource}接口以支持统一的资源操作。
 * 
 * <p>该资源将数据存储在内存字符串缓冲区中，适用于需要频繁修改
 * 或临时存储文本数据的场景，支持读取、写入和字符集转换操作。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>内存存储：数据存储在字符串缓冲区中，操作高效</li>
 *   <li>双向操作：同时支持读取({@link #getReader()})和写入({@link #getWriter()})</li>
 *   <li>自动刷新：{@link #flush()}时更新最后修改时间</li>
 *   <li>类型转换：支持字符串与字节流的自动转换({@link #getInputStream()})</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>临时文本数据存储与处理</li>
 *   <li>字符串构建与拼接（替代{@link StringBuilder}）</li>
 *   <li>需要统一资源接口的内存数据操作</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Resource
 * @see StringWriter
 */
@Getter
public final class StringBufferResource extends StringWriter implements Resource {
    /** 最后修改时间戳（每次flush时递增） */
    private long lastModified = 0;

    /**
     * 创建默认大小的字符串缓冲区资源。
     */
    public StringBufferResource() {
        super();
    }

    /**
     * 创建指定初始大小的字符串缓冲区资源。
     * 
     * @param initialSize 缓冲区初始大小
     */
    public StringBufferResource(int initialSize) {
        super(initialSize);
    }

    /**
     * 判断资源是否可读（始终返回true）。
     * 
     * @return true（内存资源始终可读）
     */
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * 判断输入流是否已解码（始终返回true）。
     * 
     * @return true（字符串资源已为字符形式）
     */
    @Override
    public boolean isDecoded() {
        return true;
    }

    /**
     * 获取字符读取器，包装字符串缓冲区内容。
     * 
     * @return 字符串读取器（{@link StringReader}）
     * @throws IOException 通常不会抛出，除非底层发生错误
     */
    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getBuffer().toString());
    }

    /**
     * 判断资源是否可写（始终返回true）。
     * 
     * @return true（内存资源始终可写）
     */
    @Override
    public boolean isWritable() {
        return true;
    }

    /**
     * 判断输出流是否已编码（始终返回true）。
     * 
     * @return true（字符串资源已为字符形式）
     */
    public boolean isEncoded() {
        return true;
    }

    /**
     * 获取字符写入器（即自身）。
     * 
     * @return 当前字符串缓冲区写入器
     * @throws IOException 通常不会抛出，除非底层发生错误
     */
    @Override
    public Writer getWriter() throws IOException {
        return this;
    }

    /**
     * 获取最后修改时间（每次flush时更新）。
     * 
     * @return 最后修改时间戳（毫秒）
     * @throws IOException 通常不会抛出
     */
    @Override
    public long lastModified() throws IOException {
        return lastModified;
    }

    /**
     * 刷新缓冲区并更新最后修改时间。
     * <p>
     * 继承自{@link StringWriter#flush()}，并在刷新后
     * 递增{@link #lastModified}时间戳。
     */
    @Override
    public void flush() {
        lastModified++;
        super.flush();
    }

    /**
     * 关闭资源，先刷新缓冲区再调用父类关闭方法。
     * 
     * @throws IOException 当关闭底层资源时发生错误
     */
    @Override
    public void close() throws IOException {
        try {
            flush();
        } finally {
            super.close();
        }
    }

    /**
     * 返回缓冲区的字符序列表示。
     * 
     * @return 字符串缓冲区的字符序列
     * @throws IOException 通常不会抛出
     */
    @Override
    public final CharSequence toCharSequence() throws IOException {
        return getBuffer();
    }

    /**
     * 获取字节输入流，将字符串转换为UTF-8字节数组。
     * 
     * @return 包含字符串内容的字节输入流
     * @throws IOException 通常不会抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getBuffer().toString().getBytes());
    }

    /**
     * 获取字节输出流（返回空输出流，不实际写入）。
     * <p>
     * 字符串资源主要用于字符操作，字节输出流返回
     * {@link NullOutputStream#NULL_OUTPUT_STREAM}。
     * 
     * @return 空输出流（不实际写入数据）
     * @throws IOException 通常不会抛出
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return NullOutputStream.NULL_OUTPUT_STREAM;
    }
}