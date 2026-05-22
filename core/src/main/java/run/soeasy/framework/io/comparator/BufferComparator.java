package run.soeasy.framework.io.comparator;

import run.soeasy.framework.io.transfer.BufferReader;

import java.io.IOException;
import java.nio.Buffer;

/**
 * 缓冲区比较器接口，用于比较两个缓冲区读取器的内容顺序。
 *
 * <p>本接口定义了一个纯粹的函数式契约：接收两个 {@link BufferReader} 作为输入，
 * 比较它们的内容顺序。实现类应关注于比较逻辑本身，
 * 而不应持有任何状态（如无必要，请勿在接口中定义记录源数据的方法）。
 *
 * <p><b>设计意图：</b>
 * 将“数据比较”这一行为抽象为标准接口，使其能与 {@link BufferTransferrer}
 * 自由组合，形成“传输-比较”的完整闭环。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * BufferComparator<ByteBuffer> comparator = new LexicographicComparator<>();
 * 
 * BufferReader<ByteBuffer> left = ...;
 * BufferReader<ByteBuffer> right = ...;
 * 
 * // 比较两个读取器
 * int result = comparator.compare(left, right);
 * }</pre>
 *
 * @param <B> 缓冲区类型，必须是 {@link Buffer} 的子类
 * @author soeasy.run
 */
public interface BufferComparator<B extends Buffer> {

    /**
     * 比较两个缓冲区读取器的内容顺序。
     *
     * <p><b>核心契约：</b>
     * <ul>
     *   <li>实现类应逐字节（或逐元素）比较两个读取器的内容</li>
     *   <li>比较过程中不应修改读取器的位置（position）或限制（limit），
     *       除非实现明确声明支持（通常建议使用副本）</li>
     *   <li>返回值语义应与 {@link java.util.Comparator} 一致：
     *       <ul>
     *         <li>负整数：左操作数小于右操作数</li>
     *         <li>零：左右操作数相等</li>
     *         <li>正整数：左操作数大于右操作数</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * @param left  第一个缓冲区读取器
     * @param right 第二个缓冲区读取器
     * @return 负整数、零或正整数，分别表示左操作数小于、等于或大于右操作数
     * @throws IOException 如果在读取过程中发生 I/O 错误
     */
    int compare(BufferReader<? super B> left, BufferReader<? super B> right) throws IOException;
}