package run.soeasy.framework.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;

/**
 * 通用包装器实现，提供对目标对象的透明包装，实现{@link Wrapper}接口。
 * 该类作为装饰器模式的基础实现，允许在不修改原始对象的前提下添加额外功能，
 * 同时保持对原始对象的访问能力。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有哈希码、字符串表示和相等性比较操作均委托给源对象</li>
 *   <li>不可变性：源对象在构造时确定后不可更改，确保线程安全</li>
 *   <li>类型安全：通过泛型确保包装器与源对象的类型一致性</li>
 *   <li>空安全：源对象通过{@code @NonNull}注解确保非空</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>功能增强：为现有对象添加日志记录、缓存等额外功能</li>
 *   <li>接口适配：将对象包装为特定接口的实现</li>
 *   <li>代理模式：作为代理对象的基础实现</li>
 *   <li>上下文附加：在不修改对象的前提下附加上下文信息</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 包装字符串对象
 * Wrapped&lt;String&gt; wrapped = new Wrapped&lt;&gt;("hello");
 * 
 * // 访问源对象
 * String source = wrapped.getSource(); // "hello"
 * 
 * // 委托操作
 * System.out.println(wrapped.toString()); // 输出 "hello"
 * System.out.println(wrapped.hashCode()); // 输出 "hello"的哈希码
 * </pre>
 *
 * @param <T> 被包装的源对象类型
 * @see Wrapper
 * @see ObjectUtils
 */
@RequiredArgsConstructor
@Getter
public class Wrapped<T> implements Wrapper<T> {
    /** 被包装的源对象，不可为null且不可变 */
    @NonNull
    private final T source;

    /**
     * 返回源对象的哈希码，委托给{@link ObjectUtils#hashCode(Object)}处理。
     *
     * @return 源对象的哈希码
     * @see ObjectUtils#hashCode(Object)
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(source);
    }

    /**
     * 返回源对象的字符串表示，委托给{@link ObjectUtils#toString(Object)}处理。
     *
     * @return 源对象的字符串表示
     * @see ObjectUtils#toString(Object)
     */
    @Override
    public String toString() {
        return ObjectUtils.toString(source);
    }

    /**
     * 判断当前包装器与另一个对象是否相等，委托给{@link ObjectUtils#equals(Object, Object)}处理。
     * <p>
     * 相等性判断逻辑：
     * <ol>
     *   <li>若另一个对象是Wrapped实例，比较其源对象</li>
     *   <li>否则直接比较源对象与另一个对象</li>
     * </ol>
     *
     * @param obj 待比较的对象
     * @return true如果源对象相等，false否则
     * @see ObjectUtils#equals(Object, Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Wrapped) {
            return ObjectUtils.equals(this.source, ((Wrapped<?>) obj).source);
        }
        return ObjectUtils.equals(this.source, obj);
    }
}