package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 映射工厂函数式接口，用于根据源对象和目标类型动态创建类型安全的映射实例。
 * <p>
 * 该接口通过泛型参数定义了映射创建的类型约束，其中：
 * <ul>
 *   <li>{@code S} 表示源对象类型</li>
 *   <li>{@code K} 表示映射键类型</li>
 *   <li>{@code V} 表示映射值类型（需实现{@link TypedValueAccessor}）</li>
 *   <li>{@code T} 表示生成的映射类型（需实现{@link Mapping<K, V>}）</li>
 * </ul>
 * 适用于需要根据运行时类型动态生成映射规则的场景，如对象转换、数据映射框架等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全的映射创建：基于{@link TypeDescriptor}进行目标类型校验</li>
 *   <li>函数式设计：作为函数式接口，支持通过lambda表达式快速实现</li>
 *   <li>默认支持判断：{@link #hasMapping(TypeDescriptor)}提供默认实现</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>类型擦除风险：泛型参数在运行时可能被擦除，导致类型安全问题</li>
 *   <li>默认实现隐患：{@link #hasMapping(TypeDescriptor)}默认返回true，可能掩盖类型不支持的情况</li>
 *   <li>线程安全：实现类需自行保证映射创建的线程安全性</li>
 *   <li>空值处理：未对源对象{@code source}进行空值校验，实现类需自行处理</li>
 * </ul>
 * </p>
 *
 * @param <S> 源对象类型
 * @param <K> 映射键类型
 * @param <V> 映射值类型，需实现{@link TypedValueAccessor}
 * @param <T> 生成的映射类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see Mapping
 * @see TypedValueAccessor
 * @see TypeDescriptor
 */
@FunctionalInterface
public interface MappingFactory<S, K, V extends TypedValueAccessor, T extends Mapping<K, V>> {

    /**
     * 判断是否支持创建指定目标类型的映射
     * <p>
     * 默认实现返回true，表示支持所有类型。子类可重写此方法，
     * 根据目标类型描述符{@code requiredType}判断是否支持创建映射。
     * 
     * @param requiredType 目标类型描述符，不可为null
     * @return true表示支持创建映射，false表示不支持
     * @throws NullPointerException 若参数为null
     */
    default boolean hasMapping(@NonNull TypeDescriptor requiredType) {
        return true;
    }

    /**
     * 根据源对象和目标类型创建映射实例
     * <p>
     * 实现类需根据源对象{@code source}和目标类型描述符{@code requiredType}
     * 生成对应的映射实例{@code T}，确保映射值类型{@code V}实现{@link TypedValueAccessor}。
     * 
     * @param source 源对象，不可为null
     * @param requiredType 目标类型描述符，不可为null
     * @return 类型安全的映射实例，不可为null
     * @throws NullPointerException 若参数为null
     * @throws IllegalArgumentException 若无法创建符合类型要求的映射
     */
    T getMapping(@NonNull S source, @NonNull TypeDescriptor requiredType);
}