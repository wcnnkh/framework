package run.soeasy.framework.core.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.ConcurrentReferenceHashMap;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 带缓存的类成员工厂，用于缓存类成员提供者以提高频繁反射操作的性能。
 * 该类包装一个基础的{@link ClassMemberFactory}实现，并使用引用哈希表缓存已创建的成员提供者，
 * 适用于需要频繁获取类成员信息的场景，如反射工具、ORM框架等。
 *
 * <p>核心特性：
 * <ul>
 *   <li>缓存机制：使用{@link ConcurrentReferenceHashMap}缓存类成员提供者，避免重复创建</li>
 *   <li>线程安全：基于ConcurrentReferenceHashMap实现线程安全的缓存操作</li>
 *   <li>引用管理：自动清理弱引用指向的过期缓存条目，防止内存泄漏</li>
 *   <li>性能优化：减少反射操作的重复开销，提升频繁类成员获取的效率</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射工具：频繁获取类的方法、字段等成员信息</li>
 *   <li>ORM框架：缓存实体类的字段映射信息</li>
 *   <li>依赖注入：缓存类的构造函数和注入点信息</li>
 *   <li>注解处理：缓存类的注解信息以避免重复扫描</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建基础的方法工厂
 * ClassMemberFactory&lt;Method&gt; methodFactory = new MethodMemberFactory();
 * 
 * // 包装为带缓存的工厂
 * ClassMemberFactory&lt;Method&gt; cachingFactory = new CachingClassMemberFactory&lt;&gt;(methodFactory);
 * 
 * // 获取User类的方法提供者（首次创建会缓存）
 * Provider&lt;Method&gt; methodProvider = cachingFactory.getClassMemberProvider(User.class);
 * 
 * // 再次获取时从缓存中读取
 * Provider&lt;Method&gt; cachedProvider = cachingFactory.getClassMemberProvider(User.class);
 * System.out.println(methodProvider == cachedProvider); // 输出: true
 * </pre>
 *
 * @param <T> 类成员的类型（如Method、Field等）
 * @see ClassMemberFactory
 * @see ConcurrentReferenceHashMap
 */
@RequiredArgsConstructor
@Getter
public class CachingClassMemberFactory<T> implements ClassMemberFactory<T> {
    /** 缓存映射表：类对象 -> 类成员提供者，使用弱引用避免内存泄漏 */
    private final ConcurrentReferenceHashMap<Class<?>, Streamable<T>> cacheMap = new ConcurrentReferenceHashMap<>();
    
    /** 被包装的基础类成员工厂，用于实际创建成员提供者 */
    @NonNull
    private final ClassMemberFactory<T> classMemberFactory;

    /**
     * 获取指定类的成员提供者，优先从缓存中获取，不存在时创建并缓存。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>尝试从缓存中获取成员提供者</li>
     *   <li>缓存缺失时，委托基础工厂创建新的提供者</li>
     *   <li>使用ConcurrentReferenceHashMap的putIfAbsent保证线程安全</li>
     *   <li>插入成功后清理未引用的缓存条目，释放内存</li>
     * </ol>
     *
     * @param declaringClass 声明成员的类，不可为null
     * @return 类成员提供者（可能来自缓存或新创建）
     */
    @Override
    public Streamable<T> getClassMemberProvider(@NonNull Class<?> declaringClass) {
    	Streamable<T> provider = cacheMap.get(declaringClass);
        if (provider == null) {
            provider = classMemberFactory.getClassMemberProvider(declaringClass);
            Streamable<T> old = cacheMap.putIfAbsent(declaringClass, provider);
            if (old == null) {
                // 仅在首次插入时清理过期缓存，减少清理频率
                cacheMap.purgeUnreferencedEntries();
            } else {
                provider = old; // 使用已存在的提供者，释放新创建的实例
            }
        }
        return provider;
    }
}