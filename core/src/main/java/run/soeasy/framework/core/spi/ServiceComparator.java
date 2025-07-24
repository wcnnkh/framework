package run.soeasy.framework.core.spi;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.comparator.OrderComparator;

/**
 * 服务比较器，继承自{@link OrderComparator}，用于服务组件的排序，
 * 提供自定义的无法比较时的处理逻辑，确保服务实例按优先级有序排列。
 * <p>
 * 该比较器在父类比较逻辑基础上，当父类返回相等结果时使用自定义的{@code unknown}值，
 * 适用于需要明确服务执行顺序的SPI（Service Provider Interface）场景。
 * </p>
 *
 * @param <T> 服务实例的类型
 * 
 * @author soeasy.run
 * @see OrderComparator
 */
@RequiredArgsConstructor
public class ServiceComparator<T> extends OrderComparator<T> {
    
    /** 默认服务比较器实例，无法比较时返回1 */
    private static final ServiceComparator<Object> DEFAULT = new ServiceComparator<>(1);
    
    /** 当父类比较结果为0时的返回值，用于处理无法明确比较的情况 */
    private final int unknown;

    /**
     * 获取默认的服务比较器实例
     * <p>
     * 默认比较器在无法明确比较时返回1，确保不同服务实例的有序性。
     * 
     * @param <T> 服务实例类型
     * @return 默认服务比较器
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceComparator<T> defaultServiceComparator() {
        return (ServiceComparator<T>) DEFAULT;
    }

    /**
     * 比较两个服务实例的顺序
     * <p>
     * 比较逻辑：
     * <ol>
     *   <li>首先判断对象是否等值（使用{@link ObjectUtils#equals}）</li>
     *   <li>若等值则返回0</li>
     *   <li>否则调用父类{@link OrderComparator#compare}获取顺序值</li>
     *   <li>若父类返回0，则返回自定义的{@code unknown}值</li>
     * </ol>
     * 
     * @param o1 第一个服务实例
     * @param o2 第二个服务实例
     * @return 比较结果：
     *         <ul>
     *           <li>负整数：o1优先于o2</li>
     *           <li>0：o1与o2等值</li>
     *           <li>正整数：o2优先于o1</li>
     *         </ul>
     */
    @Override
    public int compare(T o1, T o2) {
        if (ObjectUtils.equals(o1, o2)) {
            return 0; // 等值对象视为相等
        }
        
        int order = super.compare(o1, o2); // 调用父类顺序比较
        return order == 0 ? unknown : order; // 父类无法区分时使用自定义值
    }
}