package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 通用映射器，继承自{@link FilterableMapper}，支持根据上下文类型自动选择映射策略，
 * 可处理单键值对、数组和Map三种映射场景，实现全场景的映射转换。
 * <p>
 * 该映射器内部维护两种子映射器：
 * <ul>
 *   <li>{@link ArrayMapper}：处理数组形式的映射</li>
 *   <li>{@link MapMapper}：处理Map形式的映射</li>
 * </ul>
 * 在执行映射时，会根据源和目标上下文的类型自动选择合适的映射策略，
 * 并应用注册的过滤器链进行预处理或后处理。
 * </p>
 *
 * <p><b>映射策略：</b>
 * <ol>
 *   <li>若源和目标均为单键值对，直接调用父类处理</li>
 *   <li>若源和目标均为映射集合：
 *     <ul>
 *       <li>若源映射为Map形式，使用{@link MapMapper}处理</li>
 *       <li>否则使用{@link ArrayMapper}处理</li>
 *     </ul>
 *   </li>
 *   <li>其他情况默认调用父类处理</li>
 * </ol>
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>自动策略选择：根据上下文类型自动切换映射策略</li>
 *   <li>过滤器支持：继承父类的过滤器链机制</li>
 *   <li>自引用设计：内部映射器引用当前实例形成递归结构</li>
 *   <li>类型安全：值类型{@code V}需实现{@link TypedValueAccessor}</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see FilterableMapper
 * @see ArrayMapper
 * @see MapMapper
 */
@Getter
public class GenericMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T> {
    
    /** 处理数组形式映射的子映射器，使用当前实例作为值映射器 */
    private final ArrayMapper<K, V, T> arrayMapper = new ArrayMapper<>(this);
    
    /** 处理Map形式映射的子映射器，使用当前实例作为值映射器 */
    private final MapMapper<K, V, T> mapMapper = new MapMapper<>(this);

    /**
     * 构造通用映射器
     * 
     * @param filters 映射过滤器集合，不可为null
     * @param mapper 基础映射器，不可为null
     */
    public GenericMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters, Mapper<K, V, T> mapper) {
        super(filters, mapper);
    }

    /**
     * 执行映射转换，根据上下文类型自动选择映射策略
     * <p>
     * 该方法会判断源和目标上下文的类型，选择最合适的映射策略：
     * <ul>
     *   <li>单键值对：直接调用父类处理</li>
     *   <li>映射集合：根据源映射类型选择ArrayMapper或MapMapper</li>
     * </ul>
     * </p>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        // 处理单键值对场景
        if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
            return super.doMapping(sourceContext, targetContext);
        } 
        // 处理映射集合场景
        else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
            // 根据源映射类型选择映射策略
            if (sourceContext.getMapping().isMap()) {
                return mapMapper.doMapping(sourceContext, targetContext);
            } else {
                return arrayMapper.doMapping(sourceContext, targetContext);
            }
        }
        // 默认处理逻辑
        return super.doMapping(sourceContext, targetContext);
    }
}