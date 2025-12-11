package run.soeasy.framework.core.mapping;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.streaming.Mapping;

/**
 * 链式映射处理器，实现{@link Mapper}接口，通过责任链模式按顺序执行多个映射过滤器，
 * 支持在过滤器链执行完毕后委托给最终映射器处理。
 * <p>
 * 该类维护一个{@link MappingFilter}迭代器和最终映射器，映射流程如下：
 * <ol>
 *   <li>按迭代器顺序执行每个过滤器的{@code doMapping}方法</li>
 *   <li>过滤器可决定是否继续执行后续流程或中断映射</li>
 *   <li>当过滤器链执行完毕后，若存在最终映射器则委托其处理</li>
 * </ol>
 * 适用于需要分阶段处理映射逻辑的复杂场景，如数据验证、转换、拦截等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>责任链模式：按顺序执行多个映射过滤器</li>
 *   <li>可中断流程：过滤器可通过返回值决定是否继续映射</li>
 *   <li>最终处理器：过滤器链执行完毕后可委托给最终映射器</li>
 *   <li>类型安全：值类型{@code V}需实现{@link TypedValueAccessor}</li>
 * </ul>
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
@AllArgsConstructor
@Getter
@Setter
class ChainMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
    
    /** 映射过滤器迭代器，不可为null，按顺序执行过滤器 */
    @NonNull
    private final Iterator<? extends MappingFilter<K, V, T>> iterator;
    
    /** 最终映射器，过滤器链执行完毕后委托处理，可为null */
    private Mapper<K, V, T> mapper;

    /**
     * 执行链式映射处理
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>若迭代器有下一个过滤器，执行该过滤器的{@code doMapping}方法</li>
     *   <li>过滤器可通过返回值决定是否继续执行后续流程</li>
     *   <li>当过滤器链执行完毕且存在最终映射器时，委托其处理</li>
     *   <li>无过滤器且无最终映射器时返回false</li>
     * </ol>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        if (iterator.hasNext()) {
            // 执行下一个过滤器的映射处理，并传入自身以支持链式调用
            return iterator.next().doMapping(sourceContext, targetContext, this);
        } else if (mapper != null) {
            // 过滤器链执行完毕后，委托给最终映射器处理
            return mapper.doMapping(sourceContext, targetContext);
        }
        return false;
    }

    /**
     * 设置最终映射器（支持链式调用）
     * 
     * @param mapper 最终映射器，可为null
     * @return 当前链式映射处理器实例
     */
    public ChainMapper<K, V, T> withMapper(Mapper<K, V, T> mapper) {
        this.mapper = mapper;
        return this;
    }
}