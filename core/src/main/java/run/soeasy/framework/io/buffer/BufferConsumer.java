package run.soeasy.framework.io.buffer;

import java.io.IOException;

import lombok.NonNull;

/**
 * 数组区域消费者。
 *
 * <p>
 * 该接口定义了对数组中指定区域进行消费的操作契约。
 * 实现类通过 {@link #accept(Object, int, int)} 方法
 * 处理数组中从 {@code offset} 起始、长度为 {@code length} 的数据片段。
 *
 * <p>
 * 类型参数 {@code T} 通常为数组类型（如 {@code byte[]}、{@code char[]}、
 * {@code int[]} 等），代表一段连续内存区域。
 * {@code offset} 为数组内的起始索引，{@code length} 为要处理的
 * 元素个数，实现类应自行校验边界合法性。
 *
 * <p>
 * 该接口声明了两个异常维度：
 * <ul>
 *   <li>{@link IOException} — IO 层面的异常（如通道关闭、写入失败）</li>
 *   <li>{@code E extends Throwable} — 业务层面的自定义受检异常</li>
 * </ul>
 *
 * <p>
 * 典型使用场景：
 * <ul>
 *   <li>将数组片段写入输出通道</li>
 *   <li>对数组片段进行编码/解码转换</li>
 *   <li>将数组片段校验或转发</li>
 * </ul>
 *
 * <p>
 * 该接口为函数式接口，支持通过 lambda 表达式快速创建实例：
 *
 * <pre>{@code
 * BufferConsumer<byte[], ParseException> consumer =
 *     (array, off, len) -> {
 *         // 处理 array[off] ~ array[off + len - 1]
 *     };
 * }</pre>
 *
 * @param <T> 数组类型（如 {@code byte[]}、{@code char[]} 等）
 * @param <E> 业务层面声明的受检异常类型
 * @author soeasy.run
 */
@FunctionalInterface
public interface BufferConsumer<T, E extends Throwable> {

    /**
     * 消费数组中指定区域的数据。
     *
     * <p>
     * 实现类应处理从 {@code offset} 起始、长度为 {@code length} 的数组元素。
     * 调用方应保证 {@code offset >= 0} 且 {@code offset + length <= array.length}，
     * 但实现类可根据需要自行做额外校验。
     *
     * @param array  待消费的数组实例，不允许为 {@code null}
     * @param offset 起始索引，从数组起始位置开始计算
     * @param length 元素个数，通常应为非负数
     * @throws IOException 当 IO 操作失败时抛出
     * @throws E           当业务处理失败时抛出
     */
    void accept(@NonNull T array, int offset, int length) throws IOException, E;
}