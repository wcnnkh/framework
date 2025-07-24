package run.soeasy.framework.core.domain;

/**
 * 离散值导航接口，定义了对离散类型值的前后导航和距离计算能力。
 * 实现该接口的类型（如整数、枚举等）可通过明确的顺序关系进行值的遍历和距离度量。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型安全：泛型参数要求{@code T extends Comparable}，确保值可比较</li>
 *   <li>双向导航：提供{@link #next}和{@link #previous}方法实现前后值获取</li>
 *   <li>距离计算：通过{@link #distance}方法度量两个离散值之间的顺序间隔</li>
 *   <li>边界处理：支持离散序列的边界值（如最小值/最大值）导航</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>枚举类型遍历（如获取枚举的前一个/后一个常量）</li>
 *   <li>整数序列导航（如数值递增/递减操作）</li>
 *   <li>离散值排序与距离计算（如日期序列号、版本号等）</li>
 *   <li>有限状态机状态转移（如状态的前后切换）</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 整数离散导航实现
 * Discrete<Integer> integerDiscrete = new Discrete<Integer>() {
 *     @Override
 *     public Integer next(Integer value) { return value + 1; }
 *     @Override
 *     public Integer previous(Integer value) { return value - 1; }
 *     @Override
 *     public long distance(Integer start, Integer end) { return end - start; }
 * };
 * 
 * // 枚举离散导航实现（假设Weekday是枚举类型）
 * Discrete<Weekday> weekdayDiscrete = new Discrete<Weekday>() {
 *     @Override
 *     public Weekday next(Weekday value) {
 *         Weekday[] values = Weekday.values();
 *         int index = Arrays.binarySearch(values, value) + 1;
 *         return index < values.length ? values[index] : null;
 *     }
 *     // 其余方法实现...
 * };
 * </pre>
 *
 * @param <T> 离散值类型，必须实现{@link Comparable}接口
 */
@SuppressWarnings("rawtypes")
public interface Discrete<T extends Comparable> {
    
    /**
     * 获取指定值的下一个离散值。
     * <p>
     * 实现者需定义值的顺序关系，例如：
     * <ul>
     *   <li>对于整数，返回{@code value + 1}</li>
     *   <li>对于枚举，返回序列中的下一个常量</li>
     * </ul>
     * 若值为序列最大值，返回值由实现者决定（如null或循环到最小值）。
     *
     * @param value 当前值，不可为null
     * @return 下一个离散值，可能为null（如到达序列边界）
     */
    T next(T value);

    /**
     * 获取指定值的前一个离散值。
     * <p>
     * 实现者需定义值的顺序关系，例如：
     * <ul>
     *   <li>对于整数，返回{@code value - 1}</li>
     *   <li>对于枚举，返回序列中的前一个常量</li>
     * </ul>
     * 若值为序列最小值，返回值由实现者决定（如null或循环到最大值）。
     *
     * @param value 当前值，不可为null
     * @return 前一个离散值，可能为null（如到达序列边界）
     */
    T previous(T value);

    /**
     * 计算两个离散值之间的距离（顺序间隔）。
     * <p>
     * 距离定义为从{@code start}到{@code end}的顺序间隔数，例如：
     * <ul>
     *   <li>对于整数，返回{@code end - start}</li>
     *   <li>对于枚举，返回两个常量在序列中的索引差</li>
     * </ul>
     * 实现者需确保距离计算符合值的顺序关系。
     *
     * @param start 起始值，不可为null
     * @param end   结束值，不可为null
     * @return 从start到end的距离，可为正/负/零
     */
    long distance(T start, T end);
}