package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 映射器接口，定义基于上下文的对象映射操作，支持类型安全的值访问。
 * <p>
 * 该接口通过泛型参数约束映射过程中的键类型、值类型和映射类型，
 * 其中值类型必须实现{@link TypedValueAccessor}以支持类型化的值访问。
 * 映射器负责将源上下文中的键值对映射到目标上下文中，适用于对象转换、
 * 数据映射等需要上下文感知的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全：通过泛型约束确保映射过程中的类型一致性</li>
 *   <li>上下文感知：基于{@link MappingContext}进行映射操作，支持嵌套上下文</li>
 *   <li>值访问控制：值类型必须实现{@link TypedValueAccessor}，提供类型化值操作</li>
 * </ul>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，必须实现{@link TypedValueAccessor}
 * @param <T> 映射类型，必须实现{@link Mapping}
 * 
 * @author soeasy.run
 * @see Mapping
 * @see MappingContext
 * @see TypedValueAccessor
 */
public interface Mapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> {

    /**
     * 执行从源上下文到目标上下文的映射操作
     * <p>
     * 该方法负责将源上下文中的键值对映射到目标上下文中，
     * 具体映射逻辑由实现类决定。实现类应确保映射过程中的类型安全，
     * 并可根据上下文状态动态调整映射策略。
     * 
     * @param sourceContext 源映射上下文，包含待映射的键值对，不可为null
     * @param targetContext 目标映射上下文，用于接收映射结果，不可为null
     * @return 映射成功返回true，否则返回false
     * @throws NullPointerException 若任一参数为null
     */
    boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext, 
                      @NonNull MappingContext<K, V, T> targetContext);
}