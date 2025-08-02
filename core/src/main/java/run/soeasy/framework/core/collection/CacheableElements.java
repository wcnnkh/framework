package run.soeasy.framework.core.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;

/**
 * 可缓存的元素集合实现，支持将流式数据转换为缓存的集合对象。
 * 该类实现了Provider和CollectionElementsWrapper接口，提供了按需加载和缓存管理功能，
 * 适用于需要重复访问但不经常变更的数据集场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>懒加载机制：首次访问时才执行数据收集操作</li>
 *   <li>线程安全的缓存更新：通过synchronized保证多线程环境下的缓存一致性</li>
 *   <li>序列化支持：仅序列化缓存的集合数据，源数据流和收集器在反序列化时重新构建</li>
 *   <li>强制刷新能力：支持通过force参数强制重新加载数据</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要频繁访问但更新频率低的配置数据</li>
 *   <li>数据库查询结果的内存缓存</li>
 *   <li>远程服务调用结果的本地缓存</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <C> 缓存的集合类型，必须实现Collection&lt;E&gt;
 * @see Provider
 * @see CollectionElementsWrapper
 * @see Streamable
 */
public class CacheableElements<E, C extends Collection<E>>
        implements Provider<E>, CollectionElementsWrapper<E, C>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 缓存的集合实例，volatile保证可见性 */
    private volatile C cached;
    
    /** 元素收集器，用于将流转换为集合（transient避免序列化） */
    @NonNull
    private final transient Collector<? super E, ?, C> collector;
    
    /** 源数据流提供器（transient避免序列化） */
    @NonNull
    private final transient Streamable<? extends E> streamable;

    /**
     * 创建可缓存元素集合实例。
     * 
     * @param streamable 源数据流提供器，不可为null
     * @param collector 元素收集器，不可为null
     */
    public CacheableElements(@NonNull Streamable<? extends E> streamable,
            @NonNull Collector<? super E, ?, C> collector) {
        this.streamable = streamable;
        this.collector = collector;
    }

    /**
     * 返回自身，标识当前实例已支持缓存功能。
     * 
     * @return 当前实例
     */
    @Override
    public Provider<E> cacheable() {
        return this;
    }

    /**
     * 判断对象相等性，基于缓存的集合内容。
     * 
     * @param obj 待比较对象
     * @return 若对象相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof CacheableElements) {
            CacheableElements<?, ?> other = (CacheableElements<?, ?>) obj;
            return ObjectUtils.equals(getSource(), other.getSource());
        }
        return getSource().equals(obj);
    }

    /**
     * 获取缓存的集合实例，若未缓存则触发懒加载。
     * 
     * @return 缓存的集合实例
     */
    @Override
    public C getSource() {
        if (cached == null) {
            reload(false);
        }
        return cached;
    }

    /**
     * 返回缓存集合的哈希码。
     * 
     * @return 缓存集合的哈希码
     */
    @Override
    public int hashCode() {
        return getSource().hashCode();
    }

    /**
     * 反序列化处理，仅恢复缓存的集合数据。
     * 
     * @param input 输入流
     * @throws IOException            输入流操作异常
     * @throws ClassNotFoundException 类未找到异常
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        this.cached = (C) input.readObject();
    }

    /**
     * 重新加载数据（等效于reload(true)）。
     */
    @Override
    public void reload() {
        reload(true);
    }

    /**
     * 重新加载数据，支持强制刷新。
     * 
     * @param force 是否强制刷新（true表示忽略当前缓存，强制重新加载）
     * @return 若成功重新加载返回true，否则返回false
     */
    public boolean reload(boolean force) {
        if (collector == null || streamable == null) {
            return false;
        }

        if (cached == null || force) {
            synchronized (this) {
                if (cached == null || force) {
                    cached = streamable.collect(collector);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回缓存集合的数据流。
     * 
     * @return 缓存集合的数据流
     */
    @Override
    public Stream<E> stream() {
        return getSource().stream();
    }

    /**
     * 返回缓存集合的字符串表示。
     * 
     * @return 缓存集合的字符串表示
     */
    @Override
    public String toString() {
        return getSource().toString();
    }

    /**
     * 序列化处理，仅保存缓存的集合数据。
     * 
     * @param output 输出流
     * @throws IOException 输出流操作异常
     */
    private void writeObject(ObjectOutputStream output) throws IOException {
        output.writeObject(getSource());
    }
}