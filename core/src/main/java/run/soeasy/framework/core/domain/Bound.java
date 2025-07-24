package run.soeasy.framework.core.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.function.ValueThrowingOptional;

/**
 * 边界值模型，用于表示包含或不包含特定值的边界，支持范围检查和比较操作。
 * 该模型继承自{@link ValueThrowingOptional}，提供空安全的边界值操作，并支持通过
 * 比较器进行包含性检查和边界比较。
 *
 * <p>核心特性：
 * <ul>
 *   <li>空安全设计：通过{@link #unbounded()}支持无界场景，避免NullPointerException</li>
 *   <li>包含性控制：通过{@link #isInclusive()}区分包含边界({@code <=}/{@code >=})和不包含边界({@code <}/{@code >})</li>
 *   <li>类型转换：通过{@link #convert(Function)}支持边界值的类型转换</li>
 *   <li>范围检查：提供{@link #leftContains(Object, Comparator)}和{@link #rightContains(Object, Comparator)}
 *               进行包含性检查</li>
 *   <li>边界比较：通过{@link #compare(Bound, Comparator)}支持边界间的有序比较</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>范围查询条件（如SQL的BETWEEN操作）</li>
 *   <li>数据验证（如值必须大于某个边界）</li>
 *   <li>分页和排序（如查询大于某个ID的记录）</li>
 *   <li>资源限流（如请求量不能超过某个阈值）</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建包含100的上边界
 * Bound<Integer> upper = Bound.inclusive(100);
 * 
 * // 检查50是否小于等于100
 * boolean contains = upper.leftContains(50, Integer::compareTo);
 * 
 * // 创建不包含0的下边界
 * Bound<Long> lower = Bound.exclusive(0L);
 * 
 * // 比较两个边界
 * int result = upper.compare(lower, Integer::compareTo);
 * </pre>
 *
 * @param <T> 边界值的类型
 * @see ValueThrowingOptional
 */
public final class Bound<T> extends ValueThrowingOptional<T, RuntimeException> {
    private static final long serialVersionUID = 1L;

    /**
     * 无界实例，等价于没有边界约束。
     * 该实例的{@link #isBounded()}返回false，所有包含性检查返回true。
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final Bound<?> UNBOUNDED = new Bound(null, true);

    /**
     * 边界包含标志：
     * <ul>
     *   <li>true表示包含边界值（如<=或>=）</li>
     *   <li>false表示不包含边界值（如<或>）</li>
     * </ul>
     */
    private final boolean inclusive;

    /**
     * 私有构造函数，用于创建边界实例。
     *
     * @param value     边界值，可为null表示无界
     * @param inclusive 包含标志
     */
    private Bound(T value, boolean inclusive) {
        super(value);
        this.inclusive = inclusive;
    }

    /**
     * 获取边界包含标志。
     *
     * @return true如果边界包含该值，false否则
     */
    public boolean isInclusive() {
        return inclusive;
    }

    /**
     * 检查值是否在当前边界的左侧包含范围内。
     * <p>
     * 对于包含边界（inclusive=true），检查值是否小于等于边界值；
     * 对于不包含边界（inclusive=false），检查值是否小于边界值。
     * 无界时始终返回true。
     *
     * @param value      待检查的值，不可为null
     * @param comparator 比较器，不可为null
     * @return true如果值在左侧包含范围内，false否则
     */
    public boolean leftContains(@NonNull T value, @NonNull Comparator<T> comparator) {
        if (isBounded()) {
            int compare = comparator.compare(get(), value);
            return inclusive ? compare <= 0 : compare < 0;
        }
        return true;
    }

    /**
     * 检查值是否在当前边界的右侧包含范围内。
     * <p>
     * 对于包含边界（inclusive=true），检查值是否大于等于边界值；
     * 对于不包含边界（inclusive=false），检查值是否大于边界值。
     * 无界时始终返回true。
     *
     * @param value      待检查的值，不可为null
     * @param comparator 比较器，不可为null
     * @return true如果值在右侧包含范围内，false否则
     */
    public boolean rightContains(@NonNull T value, @NonNull Comparator<T> comparator) {
        if (isBounded()) {
            int compare = comparator.compare(get(), value);
            return inclusive ? compare >= 0 : compare > 0;
        }
        return true;
    }

    /**
     * 转换边界值的类型，创建新的边界实例。
     *
     * @param converter 类型转换函数，不可为null
     * @param <U>       目标类型
     * @return 新的边界实例，包含转换后的值和相同的包含标志
     */
    public <U> Bound<U> convert(Function<? super T, ? extends U> converter) {
        return new Bound<U>(converter.apply(orElse(null)), inclusive);
    }

    /**
     * 创建无界实例，表示没有边界约束。
     *
     * @param <T> 边界值类型
     * @return 无界实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Bound<T> unbounded() {
        return (Bound<T>) UNBOUNDED;
    }

    /**
     * 判断边界是否有界（即是否包含有效边界值）。
     *
     * @return true如果边界有界，false如果无界
     */
    public boolean isBounded() {
        return isPresent();
    }

    /**
     * 创建包含指定值的边界实例（即<=或>=）。
     *
     * @param value 边界值，不可为null
     * @param <T>   边界值类型
     * @return 包含边界实例
     */
    public static <T> Bound<T> inclusive(@NonNull T value) {
        return new Bound<>(value, true);
    }

    /**
     * 创建包含指定int值的边界实例（即<=或>=）。
     *
     * @param value 边界值
     * @return 包含边界实例
     */
    public static Bound<Integer> inclusive(int value) {
        return inclusive((Integer) value);
    }

    /**
     * 创建包含指定long值的边界实例（即<=或>=）。
     *
     * @param value 边界值
     * @return 包含边界实例
     */
    public static Bound<Long> inclusive(long value) {
        return inclusive((Long) value);
    }

    /**
     * 创建包含指定float值的边界实例（即<=或>=）。
     *
     * @param value 边界值
     * @return 包含边界实例
     */
    public static Bound<Float> inclusive(float value) {
        return inclusive((Float) value);
    }

    /**
     * 创建包含指定double值的边界实例（即<=或>=）。
     *
     * @param value 边界值
     * @return 包含边界实例
     */
    public static Bound<Double> inclusive(double value) {
        return inclusive((Double) value);
    }

    /**
     * 创建不包含指定值的边界实例（即<或>）。
     *
     * @param value 边界值，不可为null
     * @param <T>   边界值类型
     * @return 不包含边界实例
     */
    public static <T> Bound<T> exclusive(@NonNull T value) {
        return new Bound<>(value, false);
    }

    /**
     * 创建不包含指定int值的边界实例（即<或>）。
     *
     * @param value 边界值
     * @return 不包含边界实例
     */
    public static Bound<Integer> exclusive(int value) {
        return exclusive((Integer) value);
    }

    /**
     * 创建不包含指定long值的边界实例（即<或>）。
     *
     * @param value 边界值
     * @return 不包含边界实例
     */
    public static Bound<Long> exclusive(long value) {
        return exclusive((Long) value);
    }

    /**
     * 创建不包含指定float值的边界实例（即<或>）。
     *
     * @param value 边界值
     * @return 不包含边界实例
     */
    public static Bound<Float> exclusive(float value) {
        return exclusive((Float) value);
    }

    /**
     * 创建不包含指定double值的边界实例（即<或>）。
     *
     * @param value 边界值
     * @return 不包含边界实例
     */
    public static Bound<Double> exclusive(double value) {
        return exclusive((Double) value);
    }

    /**
     * 返回边界的字符串表示：
     * <ul>
     *   <li>无界时返回"unbounded"</li>
     *   <li>包含边界返回"[value]"</li>
     *   <li>不包含边界返回"(value)"</li>
     * </ul>
     *
     * @return 边界的字符串表示
     */
    @Override
    public String toString() {
        return map(Object::toString).orElse("unbounded");
    }

    /**
     * 计算边界的哈希值，考虑包含标志和边界值。
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return CollectionUtils.hashCode(Arrays.asList(inclusive, super.hashCode()));
    }

    /**
     * 判断两个边界是否相等，考虑包含标志和边界值。
     *
     * @param obj 待比较对象
     * @return true如果对象是边界且包含标志和值都相等，false否则
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Bound) {
            Bound<?> other = (Bound<?>) obj;
            return (inclusive == other.inclusive) && super.equals(obj);
        }
        return false;
    }

    /**
     * 比较两个边界的顺序，考虑包含标志和边界值。
     * <p>
     * 比较逻辑：
     * <ol>
     *   <li>先比较边界值</li>
     *   <li>边界值相等时，包含边界大于不包含边界</li>
     * </ol>
     *
     * @param other     待比较边界，不可为null
     * @param comparator 比较器，不可为null
     * @return 比较结果：负/零/正
     */
    public int compare(Bound<T> other, Comparator<? super T> comparator) {
        int value = comparator.compare(orElse(null), other.orElse(null));
        if (inclusive) {
            if (other.inclusive) {
                return value;
            } else {
                // 左侧包含的情况，相等时返回1（包含边界大于不包含边界）
                return value == 0 ? 1 : value;
            }
        } else {
            if (other.inclusive) {
                // 右侧包含的情况，相等时返回-1（不包含边界小于包含边界）
                return value == 0 ? -1 : value;
            } else {
                return value;
            }
        }
    }
}