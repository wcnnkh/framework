package run.soeasy.framework.core.transform;

import java.util.Set;

import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.spi.ServiceComparator;

/**
 * 转换器比较器，用于对{@link Transformer}实例进行排序，继承自{@link ServiceComparator}。
 * <p>
 * 该比较器优先比较{@link ConditionalTransformer}类型的转换器，通过其支持的
 * {@link TypeMapping}集合确定顺序，适用于服务发现、转换器排序等需要确定转换器优先级的场景。
 * </p>
 *
 * <p><b>比较策略：</b>
 * <ol>
 *   <li>若两个转换器均为{@link ConditionalTransformer}，比较其类型映射集合</li>
 *   <li>类型映射集合相同则返回0，否则按映射顺序比较</li>
 *   <li>仅一方为ConditionalTransformer时，ConditionalTransformer优先级更高</li>
 *   <li>非ConditionalTransformer的比较委托给父类</li>
 * </ol>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>性能损耗：使用双重循环比较类型映射集合，大数据量时效率低下</li>
 *   <li>类型安全：未对TypeMapping的compareTo结果进行安全处理，可能抛出异常</li>
 *   <li>线程安全：未考虑多线程环境下TypeMapping集合的并发修改</li>
 *   <li>映射顺序：类型映射的顺序可能影响比较结果，需确保映射定义顺序一致</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see ServiceComparator
 * @see ConditionalTransformer
 * @see TypeMapping
 */
public class TransformerComparator extends ServiceComparator<Transformer> {
    
    /** 默认的转换器比较器实例，优先级为1 */
    public static final TransformerComparator DEFAULT = new TransformerComparator(1);

    /**
     * 构造转换器比较器
     * 
     * @param unknown 未知服务的优先级值，传递给父类
     */
    public TransformerComparator(int unknown) {
        super(unknown);
    }

    /**
     * 比较两个转换器的顺序
     * <p>
     * 比较逻辑：
     * <ol>
     *   <li>若均为ConditionalTransformer，比较类型映射集合</li>
     *   <li>若只有一方为ConditionalTransformer，前者优先级更高</li>
     *   <li>否则委托给父类比较</li>
     * </ol>
     * 
     * @param o1 待比较的转换器1
     * @param o2 待比较的转换器2
     * @return 比较结果（<0表示o1<o2，0表示相等，>0表示o1>o2）
     */
    @Override
    public int compare(Transformer o1, Transformer o2) {
        if (o1 instanceof ConditionalTransformer && o2 instanceof ConditionalTransformer) {
            Set<TypeMapping> pairs = ((ConditionalTransformer) o1).getTransformableTypeMappings();
            Set<TypeMapping> otherPairs = ((ConditionalTransformer) o2).getTransformableTypeMappings();
            // 如果两个集合完全相同，返回0
            if (pairs.equals(otherPairs)) {
                return 0;
            }

            // 遍历所有映射对，找到第一个能确定顺序的比较结果
            for (TypeMapping thisMapping : pairs) {
                for (TypeMapping otherMapping : otherPairs) {
                    int comparison = thisMapping.compareTo(otherMapping);
                    if (comparison != 0) {
                        return comparison;
                    }
                }
            }
        }

        if (o1 instanceof ConditionalTransformer) {
            return -1; // ConditionalTransformer优先级更高
        }

        if (o2 instanceof ConditionalTransformer) {
            return 1;
        }

        return super.compare(o1, o2);
    }
}