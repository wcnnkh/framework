package run.soeasy.framework.core.collection;

import java.util.Arrays;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.function.Merger;

/**
 * 元素合并包装器，用于将多个Elements实例合并为统一的元素视图。
 * 该类支持通过不同的合并策略将多个元素集合合并为一个逻辑上的元素集合，
 * 适用于需要聚合多个数据源或分块数据的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>支持合并任意数量的Elements实例，通过可变参数构造</li>
 *   <li>可自定义合并策略（Merger），支持扁平合并、优先级合并等场景</li>
 *   <li>自动处理合并数组长度限制，超过限制时使用嵌套合并方式</li>
 *   <li>延迟计算合并结果，仅在需要时执行实际合并操作</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>合并多个数据库查询结果集</li>
 *   <li>聚合不同数据源的配置项</li>
 *   <li>处理分块加载的大数据集</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @see Elements
 * @see Merger
 */
public class MergedElements<E> implements ElementsWrapper<E, Elements<E>> {
    
    /**
     * 合并数组的最大长度限制，防止内存溢出。
     * 可通过系统属性 {@code run.soeasy.framework.core.collection.MergedElements.maxLength} 配置，
     * 默认值为256，最小值为4。
     */
    private static final int JOIN_MAX_LENGTH = Integer.max(4,
            Integer.getInteger(MergedElements.class.getName() + ".maxLength", 256));
    
    /** 存储待合并的Elements实例数组 */
    private final Elements<? extends E>[] members;
    
    /** 元素合并策略，决定如何合并多个Elements实例 */
    private final Merger<Elements<E>> merger;

    /**
     * 创建使用默认合并策略（扁平合并）的元素合并包装器。
     * 该构造函数使用Merger.flat()作为默认合并策略，将所有元素扁平合并为一个列表。
     *
     * @param members 待合并的Elements实例，不可为null或包含null元素
     */
    @SafeVarargs
    public MergedElements(@NonNull Elements<? extends E>... members) {
        this(Merger.flat(), members);
    }

    /**
     * 创建指定合并策略的元素合并包装器。
     *
     * @param merger 元素合并策略，不可为null
     * @param members 待合并的Elements实例，不可为null或包含null元素
     */
    @SafeVarargs
    public MergedElements(@NonNull Merger<Elements<E>> merger, @NonNull Elements<? extends E>... members) {
        this.members = members;
        this.merger = merger;
    }

    /**
     * 连接新的Elements实例，返回新的合并包装器。
     * 该方法会根据当前成员数量决定是创建新数组还是使用嵌套合并：
     * <ul>
     *   <li>若当前成员数小于JOIN_MAX_LENGTH，创建新数组添加新成员</li>
     *   <li>若达到长度限制，创建嵌套的MergedElements实例</li>
     * </ul>
     *
     * @param elements 要连接的Elements实例，不可为null
     * @return 新的MergedElements实例
     */
    @Override
    public Elements<E> concat(Elements<? extends E> elements) {
        if (members.length == JOIN_MAX_LENGTH) {
            // 数组达到最大长度时，使用嵌套合并方式避免数组过大
            return new MergedElements<>(merger, this, elements);
        } else {
            // 创建新数组添加新成员，解决大量嵌套问题
            Elements<? extends E>[] newMembers = Arrays.copyOf(members, members.length + 1);
            newMembers[members.length] = elements;
            return new MergedElements<>(merger, newMembers);
        }
    }

    /**
     * 获取合并后的元素集合。
     * 该方法通过合并策略将所有成员Elements实例合并为一个Elements实例：
     * 1. 将成员数组转换为Elements集合
     * 2. 对每个成员应用Function.identity()保持元素不变
     * 3. 使用合并策略处理所有成员，生成最终的Elements实例
     *
     * @return 合并后的Elements实例
     */
    @Override
    public Elements<E> getSource() {
        Elements<Elements<? extends E>> members = Elements.forArray(this.members);
        return merger.select(members.map((elements) -> elements.map(Function.identity())));
    }
}