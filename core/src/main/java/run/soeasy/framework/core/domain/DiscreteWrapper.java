package run.soeasy.framework.core.domain;

/**
 * 离散值导航包装器接口，用于包装{@link Discrete}实例并委托所有操作，
 * 实现装饰器模式以支持对离散值导航功能的透明增强。该接口继承自{@link Discrete}和{@link Wrapper}，
 * 允许在不修改原始离散值导航实现的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有导航方法均转发给被包装的{@link Discrete}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、缓存、验证等额外功能</li>
 *   <li>类型安全：通过泛型确保包装器与被包装离散值导航的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式创建轻量级包装器</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>导航操作日志记录：记录离散值前后导航的访问日志</li>
 *   <li>导航结果缓存：缓存频繁访问的离散值导航结果</li>
 *   <li>值范围验证：在导航前验证离散值的合法性</li>
 *   <li>性能监控：统计离散值距离计算的执行时间</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 原始离散值导航实现
 * Discrete<Integer> original = new Discrete<Integer>() {
 *     public Integer next(Integer v) { return v + 1; }
 *     public Integer previous(Integer v) { return v - 1; }
 *     public long distance(Integer s, Integer e) { return e - s; }
 * };
 * 
 * // 包装离散值导航并添加日志记录
 * DiscreteWrapper<Integer, Discrete<Integer>> logged = value -> {
 *     System.out.println("Navigate: " + value);
 *     return original;
 * };
 * 
 * // 使用包装后的导航功能
 * Integer nextValue = logged.next(10); // 输出日志并返回11
 * </pre>
 *
 * @param <T> 离散值类型，必须实现{@link Comparable}接口
 * @param <W> 被包装的离散值导航类型，必须是{@link Discrete}的子类型
 * @see Discrete
 * @see Wrapper
 */
@SuppressWarnings("rawtypes")
public interface DiscreteWrapper<T extends Comparable, W extends Discrete<T>> extends Discrete<T>, Wrapper<W> {
    
    /**
     * 获取指定值的下一个离散值，转发给被包装的Discrete实例。
     *
     * @param value 当前值，不可为null
     * @return 下一个离散值，可能为null（如到达序列边界）
     * @see Discrete#next(Object)
     */
    @Override
    default T next(T value) {
        return getSource().next(value);
    }

    /**
     * 获取指定值的前一个离散值，转发给被包装的Discrete实例。
     *
     * @param value 当前值，不可为null
     * @return 前一个离散值，可能为null（如到达序列边界）
     * @see Discrete#previous(Object)
     */
    @Override
    default T previous(T value) {
        return getSource().previous(value);
    }

    /**
     * 计算两个离散值之间的距离，转发给被包装的Discrete实例。
     *
     * @param start 起始值，不可为null
     * @param end   结束值，不可为null
     * @return 从start到end的距离，可为正/负/零
     * @see Discrete#distance(Object, Object)
     */
    @Override
    default long distance(T start, T end) {
        return getSource().distance(start, end);
    }
}