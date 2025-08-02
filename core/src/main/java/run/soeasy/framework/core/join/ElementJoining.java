package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 元素连接器实现，用于将元素序列按指定分隔符连接成一个字符序列。
 * 该实现通过提供的编码器函数将元素转换为字符序列，并在元素间添加分隔符。
 *
 * <p>核心特性：
 * <ul>
 *   <li>线程安全：无状态设计，可安全用于多线程环境</li>
 *   <li>空值处理：自动跳过编码器返回null的元素</li>
 *   <li>灵活编码：通过注入的编码器函数支持任意类型元素的转换</li>
 *   <li>高效连接：使用Appendable接口支持多种输出目标</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>集合元素转字符串（如List转CSV格式）</li>
 *   <li>生成格式化文本（如SQL IN子句参数列表）</li>
 *   <li>自定义协议消息组装</li>
 *   <li>日志格式化输出</li>
 * </ul>
 *
 * @param <E> 待连接的元素类型
 * @see Joiner
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ElementJoining<E> implements Joiner<E> {
    
    /**
     * 元素间的分隔符，不可变且不可为null。
     */
    private final CharSequence delimiter;
    
    /**
     * 元素编码器，将元素转换为字符序列，不可为null。
     */
    @NonNull
    private final Function<? super E, ? extends CharSequence> encoder;

    /**
     * 将单个元素连接到目标Appendable。
     * 若编码器返回null，则跳过该元素；否则在非首个元素前添加分隔符，
     * 然后追加编码后的元素内容。
     *
     * @param appendable 目标输出对象
     * @param count      当前已连接的元素数量
     * @param element    待连接的元素
     * @return 成功连接的元素数量（0或1）
     * @throws IOException 当输出操作失败时抛出
     */
    @Override
    public long join(Appendable appendable, long count, E element) throws IOException {
        CharSequence charSequence = encoder.apply(element);
        if (charSequence == null) {
            return 0;
        }

        if (count != 0) {
            appendable.append(delimiter);
        }
        appendable.append(charSequence);
        return 1;
    }
}