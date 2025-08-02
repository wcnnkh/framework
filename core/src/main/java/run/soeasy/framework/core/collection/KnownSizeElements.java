package run.soeasy.framework.core.collection;

import java.util.function.ToLongFunction;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 已知大小的元素集合包装器，用于为元素集合提供预先计算的大小统计功能。
 * 该类通过包装原始元素集合并应用指定的大小统计函数，避免重复计算元素数量，提升性能。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过ToLongFunction预定义元素数量统计逻辑，避免重复计算</li>
 *   <li>所有大小相关操作（count/isEmpty/isUnique）均基于预定义的统计函数</li>
 *   <li>保持与原始元素集合完全一致的行为，仅增强大小统计能力</li>
 *   <li>不可变设计，确保统计逻辑的一致性和线程安全性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>元素数量计算代价较高的场景（如数据库查询结果集）</li>
 *   <li>需要频繁获取元素数量但数据不常变更的场景</li>
 *   <li>需要统一元素数量统计逻辑的标准化处理场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的元素集合类型，必须实现Elements接口
 * @see ElementsWrapper
 * @see ToLongFunction
 */
@RequiredArgsConstructor
@Getter
public class KnownSizeElements<E, W extends Elements<E>> implements ElementsWrapper<E, W> {
    
    /** 被包装的原始元素集合 */
    @NonNull
    private final W source;
    
    /** 元素数量统计函数，用于预定义大小计算逻辑 */
    @NonNull
    private final ToLongFunction<? super W> statisticsSize;

    /**
     * 返回元素数量，通过预定义的统计函数计算。
     * 该方法直接调用statisticsSize函数，避免重复计算元素数量。
     *
     * @return 元素数量
     */
    @Override
    public long count() {
        return statisticsSize.applyAsLong(source);
    }

    /**
     * 判断元素集合是否为空。
     * 基于count()结果判断，若元素数量为0则返回true。
     *
     * @return 若元素数量为0返回true，否则返回false
     */
    @Override
    public boolean isEmpty() {
        return count() == 0;
    }

    /**
     * 判断元素集合是否只包含一个元素。
     * 基于count()结果判断，若元素数量为1则返回true。
     *
     * @return 若元素数量为1返回true，否则返回false
     */
    @Override
    public boolean isUnique() {
        return count() == 1;
    }
}