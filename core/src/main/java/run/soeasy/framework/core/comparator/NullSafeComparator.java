package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import lombok.NonNull;

/**
 * 空安全比较器
 * 为原始Comparator添加null值处理能力，支持配置null值的排序优先级
 * 
 * 核心特性：
 * 1. 处理null值与非null值的比较
 * 2. 可配置null值是排在前面（nullsLow=true）还是后面
 * 3. 保留原始Comparator的非null值比较逻辑
 * 
 * @author soeasy.run
 */
public class NullSafeComparator<T> implements Comparator<T> {
    private final Comparator<T> nonNullComparator;  // 原始非空比较器
    private final boolean nullsLow;                 // null值是否排在前面

    /**
     * 构造函数
     * 
     * @param comparator 原始非空比较器（必须非null）
     * @param nullsLow   null值排序策略：
     *                   true表示null值小于非null值（排在前面）
     *                   false表示null值大于非null值（排在后面）
     */
    public NullSafeComparator(@NonNull Comparator<T> comparator, boolean nullsLow) {
        this.nonNullComparator = comparator;
        this.nullsLow = nullsLow;
    }

    /**
     * 比较两个对象，支持null值安全比较
     * 
     * @param o1 第一个比较对象
     * @param o2 第二个比较对象
     * @return 负整数表示o1小于o2，零表示相等，正整数表示o1大于o2
     */
    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;                          // 相同对象直接返回0
        }
        if (o1 == null) {
            return (this.nullsLow ? -1 : 1);    // o1为null时的处理
        }
        if (o2 == null) {
            return (this.nullsLow ? 1 : -1);    // o2为null时的处理
        }
        return this.nonNullComparator.compare(o1, o2);  // 非null值委托原始比较器
    }

    /**
     * 比较器相等性判断
     * 当原始比较器和null排序策略都相同时视为相等
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NullSafeComparator)) {
            return false;
        }
        NullSafeComparator<T> other = (NullSafeComparator<T>) obj;
        return (this.nonNullComparator.equals(other.nonNullComparator) && this.nullsLow == other.nullsLow);
    }

    /**
     * 生成哈希码
     * 结合原始比较器哈希码和null排序策略生成
     */
    @Override
    public int hashCode() {
        return (this.nullsLow ? -1 : 1) * this.nonNullComparator.hashCode();
    }

    /**
     * 字符串表示
     * 显示原始比较器和null排序策略
     */
    @Override
    public String toString() {
        return "NullSafeComparator: non-null comparator [" + this.nonNullComparator + "]; "
                + (this.nullsLow ? "nulls low" : "nulls high");
    }
}