package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 键值对连接器实现，用于将{@link KeyValue}对象按指定格式连接成字符序列。
 * 该实现支持自定义键值对之间的分隔符、键与值之间的连接符，
 * 并通过编码器函数实现键值的灵活转换。
 *
 * <p>核心特性：
 * <ul>
 *   <li>格式定制：支持自定义键值对分隔符({@link #delimiter})和键值连接符({@link #connector})</li>
 *   <li>类型转换：通过独立的键编码器({@link #keyEncoder})和值编码器({@link #valueEncoder})支持任意类型键值的转换</li>
 *   <li>空值处理：自动跳过键或值为null的{@link KeyValue}对象</li>
 *   <li>高效连接：基于{@link Appendable}接口实现高性能的字符序列拼接</li>
 *   <li>线程安全：无状态设计，可安全用于多线程环境</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>生成URL查询参数（如key1=value1&key2=value2）</li>
 *   <li>格式化配置项（如property1=value1;property2=value2）</li>
 *   <li>组装HTTP请求头（如Header1: Value1, Header2: Value2）</li>
 *   <li>自定义协议消息的键值对部分组装</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see Joiner
 * @see KeyValue
 */
@RequiredArgsConstructor
@Getter
@Setter
public class KeyValueJoiner<K, V> implements Joiner<KeyValue<? extends K, ? extends V>> {
    
    /**
     * 键值对之间的分隔符（如"&"、";"），不可变且不可为null。
     * <p>
     * 示例：多个键值对"key1=value1"和"key2=value2"之间使用","分隔，
     * 最终结果为"key1=value1,key2=value2"。
     */
    private final CharSequence delimiter;
    
    /**
     * 键与值之间的连接符（如"="、":"），不可变且不可为null。
     * <p>
     * 示例：键"key"和值"value"使用"="连接，结果为"key=value"。
     */
    private final CharSequence connector;
    
    /**
     * 键的编码器，将键转换为字符序列，不可为null。
     * <p>
     * 该编码器会被应用于{@link KeyValue#getKey()}的返回值。
     */
    @NonNull
    private final Function<? super K, ? extends CharSequence> keyEncoder;
    
    /**
     * 值的编码器，将值转换为字符序列，不可为null。
     * <p>
     * 该编码器会被应用于{@link KeyValue#getValue()}的返回值。
     */
    @NonNull
    private final Function<? super V, ? extends CharSequence> valueEncoder;

    /**
     * 将单个键值对连接到目标Appendable。
     * <p>
     * 执行逻辑：
     * <ol>
     *   <li>使用{@link #keyEncoder}和{@link #valueEncoder}转换键值</li>
     *   <li>若键或值为null，跳过该键值对（返回0）</li>
     *   <li>非首个键值对前添加{@link #delimiter}</li>
     *   <li>拼接键、{@link #connector}、值</li>
     *   <li>返回1表示成功连接</li>
     * </ol>
     *
     * @param appendable 目标输出对象（如StringBuilder）
     * @param count 当前已连接的键值对数量（用于判断是否添加分隔符）
     * @param element 待连接的键值对对象
     * @return 成功连接的键值对数量（0或1）
     * @throws IOException 当输出操作失败时抛出
     */
    @Override
    public long join(Appendable appendable, long count, KeyValue<? extends K, ? extends V> element) throws IOException {
        CharSequence key = keyEncoder.apply(element.getKey());
        CharSequence value = valueEncoder.apply(element.getValue());
        
        // 跳过键或值为null的情况
        if (key == null || value == null) {
            return 0;
        }
        
        // 非首个元素前添加分隔符
        if (count != 0) {
            appendable.append(delimiter);
        }
        
        // 拼接键、连接符、值
        appendable.append(key)
                .append(connector)
                .append(value);
                
        return 1;
    }
}