package run.soeasy.framework.core.convert;

import java.util.Set;

import run.soeasy.framework.core.spi.ServiceComparator;

/**
 * 转换器比较器，用于对实现{@link Converter}接口的组件进行排序。
 * 该比较器定义了转换器的优先级规则，确保在类型转换时能选择最合适的转换器：
 *
 * <p>排序规则：
 * <ol>
 *   <li>条件型转换器（实现{@link ConditionalConverter}）优先于普通转换器</li>
 *   <li>条件型转换器之间通过比较{@link TypeMapping}集合确定顺序</li>
 *   <li>类型映射对的比较基于{@link TypeMapping#compareTo(TypeMapping)}方法</li>
 *   <li>其他情况使用{@link ServiceComparator}的默认排序规则</li>
 * </ol>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>自动发现多个转换器时的优先级排序</li>
 *   <li>在转换器链中确定转换器的执行顺序</li>
 *   <li>实现SPI服务的有序加载</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Converter
 * @see ConditionalConverter
 * @see TypeMapping
 * @see ServiceComparator
 */
public class ConverterComparator extends ServiceComparator<Converter> {

    /**
     * 默认比较器实例，未知优先级值设为1
     */
    public static final ConverterComparator DEFAULT = new ConverterComparator(1);

    /**
     * 创建具有指定未知优先级值的比较器
     * 
     * @param unknown 未知优先级值，用于没有明确优先级的转换器
     */
    public ConverterComparator(int unknown) {
        super(unknown);
    }

    /**
     * 比较两个转换器的优先级
     * 
     * @param o1 第一个转换器
     * @param o2 第二个转换器
     * @return 比较结果：
     *         - 负数：o1优先级高于o2
     *         - 零：两者优先级相同
     *         - 正数：o1优先级低于o2
     */
    @Override
    public int compare(Converter o1, Converter o2) {
        // 处理两个都是条件型转换器的情况
        if (o1 instanceof ConditionalConverter && o2 instanceof ConditionalConverter) {
            Set<TypeMapping> pairs = ((ConditionalConverter) o1).getConvertibleTypeMappings();
            Set<TypeMapping> otherPairs = ((ConditionalConverter) o2).getConvertibleTypeMappings();
            
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

        // 条件型转换器优先于普通转换器
        if (o1 instanceof ConditionalConverter) {
            return -1;
        }

        if (o2 instanceof ConditionalConverter) {
            return 1;
        }
        
        // 其他情况使用父类比较器
        return super.compare(o1, o2);
    }
}