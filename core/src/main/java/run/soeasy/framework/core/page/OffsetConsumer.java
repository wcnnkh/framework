package run.soeasy.framework.core.page;

/**
 * 带偏移量与长度的消费者函数接口。
 *
 * <p>与 {@link java.util.function.Consumer} 不同，该接口允许在消费数据时
 * 指定操作的起始偏移量 {@code offset} 与处理长度 {@code length}，
 * 适用于对数组、缓冲区或其他线性数据结构进行局部处理。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>支持对任意类型 {@code T} 的数据源进行消费</li>
 *   <li>通过 {@code offset + length} 精确控制处理范围</li>
 *   <li>支持抛出受检异常，适配 IO、解析等失败场景</li>
 *   <li>函数式接口，可直接使用 Lambda 或方法引用</li>
 * </ul>
 *
 * <p><b>常见使用场景：</b>
 * <ul>
 *   <li>按段处理数组或内存块（零拷贝）</li>
 *   <li>流式或管道化数据处理</li>
 *   <li>协议解析、编解码、序列化</li>
 *   <li>大对象的分片消费</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 数据源类型（如 byte[]、ByteBuffer、自定义对象等）
 * @param <E> 消费过程中可能抛出的异常类型
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface OffsetConsumer<T, E extends Throwable> {

    /**
     * 消费指定数据源中给定区间的数据。
     *
     * @param source 数据源（具体实现可决定是否允许 null）
     * @param offset 起始偏移量
     * @param length 要处理的数据长度
     * @throws E 当消费过程中发生异常时抛出
     */
    void accept(T source, int offset, int length) throws E;
}