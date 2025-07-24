package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;

import lombok.NonNull;

/**
 * 集合元素包装类，用于将流式数据转换为缓存的集合对象并提供集合操作接口。
 * 该类继承自CacheableElements，在缓存机制基础上额外提供标准集合操作能力，
 * 适用于需要频繁通过集合接口访问但不经常变更的数据集场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承CacheableElements的缓存机制和懒加载特性</li>
 *   <li>实现标准集合操作接口（iterator/size），直接委托给缓存的集合实例</li>
 *   <li>构造时强制加载数据（reload(true)），确保初始状态为最新数据</li>
 *   <li>通过Collector自定义集合实现类型（如ArrayList/HashSet等）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要同时通过集合接口和流式接口访问的数据</li>
 *   <li>初始化时即需要加载全部数据的缓存场景</li>
 *   <li>需要自定义集合实现类型的场景（通过Collector参数指定）</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <C> 缓存的集合类型，必须实现Collection&lt;E&gt;
 * @see CacheableElements
 * @see Streamable
 * @see Collector
 */
public class CollectionElements<E, C extends Collection<E>> extends CacheableElements<E, C> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建集合元素包装实例，强制加载数据并缓存。
     * 该构造函数会立即执行数据收集操作（reload(true)），确保缓存的集合实例被初始化。
     *
     * @param streamable 源数据流提供器，不可为null
     * @param collector  元素收集器，用于指定目标集合类型，不可为null
     */
    public CollectionElements(@NonNull Streamable<E> streamable, @NonNull Collector<? super E, ?, C> collector) {
        super(streamable, collector);
        reload(true);
    }

    /**
     * 返回缓存集合的迭代器。
     * 该方法直接委托给缓存的集合实例的iterator()方法，
     * 确保与原生集合的迭代行为一致。
     *
     * @return 缓存集合的迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return getSource().iterator();
    }

    /**
     * 返回缓存集合的元素数量。
     * 该方法直接委托给缓存的集合实例的size()方法，
     * 确保与原生集合的尺寸查询行为一致。
     *
     * @return 缓存集合中的元素数量
     */
    @Override
    public int size() {
        return getSource().size();
    }
}