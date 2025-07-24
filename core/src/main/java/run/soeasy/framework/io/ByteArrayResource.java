package run.soeasy.framework.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 字节数组资源实现类，基于{@link ByteArrayOutputStream}实现内存字节数组资源，
 * 实现{@link Resource}接口以支持统一的资源操作。
 * 
 * <p>该资源将数据存储在内存字节数组中，适用于需要临时存储二进制数据的场景，
 * 支持读取、写入和获取资源元数据等操作，所有数据操作均在内存中完成。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>内存存储：数据存储在字节数组中，操作高效</li>
 *   <li>双向操作：同时支持读取({@link #getInputStream()})和写入({@link #getOutputStream()})</li>
 *   <li>自动扩容：继承自ByteArrayOutputStream，支持动态扩容</li>
 *   <li>元数据跟踪：记录最后修改时间，每次刷新时更新</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>临时数据存储：缓存二进制数据，如网络传输中间数据</li>
 *   <li>内存数据操作：需要在内存中处理字节数据的场景</li>
 *   <li>数据转换：作为字节数据的中转站，支持快速转换为输入流</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Resource
 * @see ByteArrayOutputStream
 */
public final class ByteArrayResource extends ByteArrayOutputStream implements Resource {
    /** 最后修改时间戳，每次flush时递增 */
    private int lastModified = 0;

    /**
     * 创建默认大小的字节数组资源（初始容量为32字节）。
     */
    public ByteArrayResource() {
        super();
    }

    /**
     * 创建指定初始大小的字节数组资源。
     * 
     * @param initialSize 初始容量（字节数）
     */
    public ByteArrayResource(int initialSize) {
        super(initialSize);
    }

    /**
     * 判断资源是否存在（始终返回true）。
     * <p>
     * 内存资源始终存在，因此直接返回true。
     * 
     * @return true
     */
    @Override
    public boolean exists() {
        return true;
    }

    /**
     * 同步获取当前字节数组内容。
     * <p>
     * 重写父类方法并添加同步修饰，确保线程安全。
     * 
     * @return 包含当前数据的字节数组
     */
    @Override
    public synchronized byte[] toByteArray() {
        return super.toByteArray();
    }

    /**
     * 获取资源内容长度（字节数）。
     * <p>
     * 返回当前字节数组中已写入的字节数，等价于父类的{@link #count}字段。
     * 
     * @return 内容长度（字节）
     * @throws IOException 通常不会抛出，保持接口一致性
     */
    @Override
    public long contentLength() throws IOException {
        return count;
    }

    /**
     * 判断资源是否可读（始终返回true）。
     * 
     * @return true
     */
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * 获取资源输入流。
     * <p>
     * 返回一个新的ByteArrayInputStream，包装当前字节数组内容，
     * 支持从当前已写入的数据开始读取。
     * 
     * @return 字节数组输入流
     * @throws IOException 通常不会抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(buf, 0, count);
    }

    /**
     * 判断资源是否可写（始终返回true）。
     * 
     * @return true
     */
    @Override
    public boolean isWritable() {
        return true;
    }

    /**
     * 获取资源输出流（即自身）。
     * <p>
     * 直接返回当前ByteArrayResource实例，所有写入操作将数据追加到字节数组。
     * 
     * @return 当前资源实例（OutputStream类型）
     * @throws IOException 通常不会抛出
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this;
    }

    /**
     * 刷新资源并更新最后修改时间。
     * <p>
     * 调用父类flush方法，并递增{@link #lastModified}时间戳。
     * 
     * @throws IOException 当刷新底层流时发生异常
     */
    @Override
    public void flush() throws IOException {
        lastModified++;
        super.flush();
    }

    /**
     * 关闭资源，先刷新再调用父类关闭方法。
     * 
     * @throws IOException 当关闭底层流时发生异常
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
     * 获取资源最后修改时间。
     * <p>
     * 返回{@link #lastModified}字段值，每次flush时更新。
     * 
     * @return 最后修改时间戳（简化为int类型）
     * @throws IOException 通常不会抛出
     */
    @Override
    public long lastModified() throws IOException {
        return lastModified;
    }
}