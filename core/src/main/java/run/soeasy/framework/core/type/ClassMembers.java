package run.soeasy.framework.core.type;

import java.io.Serializable;
import java.util.Collections;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ReloadableElementsWrapper;

/**
 * 类成员解析器，用于加载和管理类的成员（如字段、方法、构造函数等），
 * 实现{@link ReloadableElementsWrapper}接口以支持成员的延迟加载和重新加载。
 * 该类通过函数式接口动态加载类成员，支持从父类和接口继承成员的解析，
 * 适用于反射操作、类结构分析、框架元数据管理等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>延迟加载：成员数据在首次访问时加载，提高初始化性能</li>
 *   <li>可重新加载：支持强制重新加载成员数据，适应类结构动态变化场景</li>
 *   <li>继承支持：可获取父类和接口的成员，构建完整的类成员体系</li>
 *   <li>函数式加载：通过自定义函数灵活实现不同类型成员的加载逻辑</li>
 *   <li>类型安全：使用泛型确保加载成员的类型一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射工具：构建类成员索引，加速反射操作</li>
 *   <li>ORM框架：解析实体类字段和方法用于映射关系</li>
 *   <li>依赖注入：分析类依赖关系和可注入成员</li>
 *   <li>代码生成：根据类成员结构生成相关代码或配置</li>
 *   <li>元数据管理：收集和管理类的元数据信息</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 定义字段加载器
 * Function&lt;Class&lt;?&gt;, Elements&lt;Field&gt;&gt; fieldLoader = 
 *     clazz -&gt; Elements.of(clazz.getDeclaredFields());
 * 
 * // 创建类成员解析器
 * ClassMembers&lt;Field&gt; classMembers = new ClassMembers&lt;&gt;(User.class, fieldLoader);
 * 
 * // 获取字段列表
 * Elements&lt;Field&gt; fields = classMembers.getSource();
 * System.out.println("字段数量: " + fields.size());
 * 
 * // 获取父类成员
 * ClassMembers&lt;Field&gt; superclassMembers = classMembers.getSuperclass();
 * if (superclassMembers != null) {
 *     Elements&lt;Field&gt; superFields = superclassMembers.getSource();
 *     System.out.println("父类字段数量: " + superFields.size());
 * }
 * </pre>
 *
 * @param <E> 类成员的类型（如Field, Method, Constructor等）
 * @see ReloadableElementsWrapper
 * @see Elements
 */
@RequiredArgsConstructor
public class ClassMembers<E> implements ReloadableElementsWrapper<E, Elements<E>>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 声明成员的类对象 */
    @NonNull
    @Getter
    private final Class<?> declaringClass;
    
    /** 成员加载函数，输入类对象，输出成员元素集合 */
    @NonNull
    @Getter
    private final Function<? super Class<?>, ? extends Elements<E>> loader;
    
    /** 缓存的成员元素集合，volatile保证可见性 */
    private volatile transient Elements<E> source;

    /**
     * 获取类成员元素集合，自动触发加载（若未加载）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>检查缓存是否存在，不存在则触发加载</li>
     *   <li>返回加载后的成员集合，空集合会被转换为Elements.empty()</li>
     * </ol>
     *
     * @return 类成员的Elements集合，不会为null
     */
    @Override
    public Elements<E> getSource() {
        reload(false);
        return source;
    }

    /**
     * 重新加载类成员数据，支持强制加载。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>若force为true或缓存为空，进入同步块重新加载</li>
     *   <li>调用loader函数获取新的成员集合</li>
     *   <li>空结果会被转换为Elements.empty()保证非空</li>
     *   <li>返回是否成功加载新数据</li>
     * </ol>
     *
     * @param force 是否强制重新加载，true表示忽略缓存直接加载
     * @return true如果成功加载了新数据，false如果使用缓存数据
     */
    public boolean reload(boolean force) {
        if (source == null || force) {
            synchronized (this) {
                if (source == null || force) {
                    this.source = loader.apply(declaringClass);
                    this.source = source == null ? Elements.empty() : source;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 强制重新加载类成员数据（等价于reload(true)）。
     */
    @Override
    public void reload() {
        reload(true);
    }

    /**
     * 获取父类的成员解析器。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>获取当前类的父类Class对象</li>
     *   <li>若父类存在，使用相同的loader创建父类成员解析器</li>
     *   <li>父类为null（如Object类）时返回null</li>
     * </ol>
     *
     * @return 父类的ClassMembers实例，若无父类返回null
     */
    public ClassMembers<E> getSuperclass() {
        Class<?> superclass = declaringClass.getSuperclass();
        return superclass == null ? null : new ClassMembers<>(superclass, loader);
    }

    /**
     * 获取当前类实现的所有接口的成员解析器集合。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>获取当前类实现的所有接口Class数组</li>
     *   <li>将每个接口转换为对应的ClassMembers实例</li>
     *   <li>返回接口成员解析器的Elements集合</li>
     * </ol>
     *
     * @return 接口成员解析器的Elements集合，可能为空集合
     */
    public Elements<ClassMembers<E>> getInterfaces() {
        return Elements.of(() -> {
            Class<?>[] interfaces = declaringClass.getInterfaces();
            if (interfaces == null) {
                return Collections.emptyIterator();
            }

            return Elements.forArray(interfaces)
                    .map((e) -> new ClassMembers<>(e, loader))
                    .iterator();
        });
    }
}