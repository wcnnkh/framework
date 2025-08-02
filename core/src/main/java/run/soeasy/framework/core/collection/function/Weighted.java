package run.soeasy.framework.core.collection.function;

/**
 * 权重接口，用于标识具有权重属性的对象。
 * 实现此接口的类可用于权重相关的算法（如权重选择、负载均衡、优先级排序等），
 * 权重值通常为非负整数，值越大表示权重越高。
 *
 * @author soeasy.run
 */
public interface Weighted {
    
    /**
     * 获取对象的权重值。
     * 权重值通常应是非负整数，数值越大表示权重越高（如在选择算法中被选中的概率更高）。
     *
     * @return 权重值，建议为非负整数
     */
    int getWeight();
}