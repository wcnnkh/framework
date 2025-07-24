package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 可过滤的映射器，实现{@link Mapper}接口，支持在映射过程中应用多个过滤器进行预处理或后处理。
 * <p>
 * 该映射器通过责任链模式按顺序执行注册的过滤器，每个过滤器可以：
 * <ul>
 *   <li>修改映射上下文</li>
 *   <li>中断映射流程</li>
 *   <li>执行额外的验证或转换逻辑</li>
 * </ul>
 * 所有过滤器执行完毕后，最终由内部的基础映射器执行实际的映射操作。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>过滤器链：按顺序执行多个过滤器，形成处理流水线</li>
 *   <li>可组合性：支持嵌套创建新的FilterableMapper实例</li>
 *   <li>延迟执行：过滤器链在实际调用doMapping时才会执行</li>
 *   <li>类型安全：值类型{@code V}需实现{@link TypedValueAccessor}</li>
 * </ul>
 * </p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * FilterableMapper<K, V, T> mapper = new FilterableMapper<>(filters, baseMapper);
 * mapper.doMapping(sourceContext, targetContext);
 * 
 * // 临时添加额外过滤器
 * mapper.doMapping(sourceContext, targetContext, extraFilters);
 * }</pre>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see Mapper
 * @see MappingFilter
 * @see TypedValueAccessor
 */
@RequiredArgsConstructor
@Getter
public class FilterableMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
    
    /** 映射过滤器集合，不可为null，按迭代顺序执行 */
    @NonNull
    private final Iterable<MappingFilter<K, V, T>> filters;
    
    /** 基础映射器，在所有过滤器执行后调用，不可为null */
    @NonNull
    private final Mapper<K, V, T> mapper;

    /**
     * 执行带过滤的映射转换
     * <p>
     * 该方法创建一个{@link ChainMapper}实例，将过滤器集合转换为迭代器，
     * 并设置基础映射器为最终处理器，然后执行链式映射处理。
     * </p>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        // 创建链式映射器，将过滤器集合转换为迭代器，并设置基础映射器为最终处理器
        ChainMapper<K, V, T> chain = new ChainMapper<>(filters.iterator(), mapper);
        return chain.doMapping(sourceContext, targetContext);
    }

    /**
     * 使用额外的过滤器执行映射转换
     * <p>
     * 该方法创建一个新的{@link FilterableMapper}实例，将传入的过滤器集合与当前实例的过滤器组合，
     * 并将当前实例作为基础映射器，实现临时添加额外过滤器的功能。
     * </p>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @param filters 额外的过滤器集合，不可为null
     * @return 映射成功返回true，否则false
     */
    public final boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                                   @NonNull MappingContext<K, V, T> targetContext,
                                   @NonNull Iterable<MappingFilter<K, V, T>> filters) {
        // 创建新的可过滤映射器，组合额外过滤器和当前实例，并执行映射
        FilterableMapper<K, V, T> templateWriter = new FilterableMapper<>(filters, this);
        return templateWriter.doMapping(sourceContext, targetContext);
    }
}