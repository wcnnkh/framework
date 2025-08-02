package run.soeasy.framework.core.collection.function;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;

/**
 * 带权重的源对象包装器，用于为任意对象赋予权重属性。
 * 该类继承自Wrapped类，允许将原始对象包装为具有权重的对象，
 * 从而可以在需要权重计算的场景中使用，如负载均衡、优先级排序等。
 *
 * @author soeasy.run
 * @param <W> 被包装的源对象类型
 * @see Wrapped
 * @see Weighted
 */
@Getter
public class WeightedSource<W> extends Wrapped<W> implements Weighted {
    
    /**
     * 对象的权重值，不可变且不可为负。
     * 权重值越大，表示该对象在权重相关算法中具有更高的优先级或更大的概率被选中。
     */
    private final int weight;

    /**
     * 创建带权重的源对象包装器。
     *
     * @param source 被包装的源对象，不可为null
     * @param weight 对象的权重值，必须为非负整数
     */
    public WeightedSource(@NonNull W source, int weight) {
        super(source);
        this.weight = weight;
    }
}