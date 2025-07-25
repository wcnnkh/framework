package run.soeasy.framework.core.transform.property;

import java.util.function.BiPredicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.templates.Mapper;
import run.soeasy.framework.core.transform.templates.MappingContext;

/**
 * 属性映射谓词过滤器，实现{@link PropertyMappingFilter}接口，
 * 通过BiPredicate对源属性和目标属性进行条件判断，控制属性映射流程。
 * <p>
 * 该过滤器在属性映射过程中，根据传入的谓词条件决定是否执行映射操作：
 * <ul>
 *   <li>当谓词返回true时，继续执行后续映射流程</li>
 *   <li>当谓词返回false时，终止当前属性的映射，直接返回false</li>
 * </ul>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>谓词驱动：基于BiPredicate实现灵活的过滤逻辑</li>
 *   <li>上下文感知：可访问完整的映射上下文信息</li>
 *   <li>链式组合：可与其他过滤器组合形成复杂过滤链</li>
 *   <li>空值安全：在执行谓词前会检查上下文是否包含有效键值</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性值条件过滤（如忽略空值、特定值）</li>
 *   <li>属性元数据条件过滤（如基于属性名称、类型）</li>
 *   <li>动态过滤规则（运行时根据上下文决定过滤条件）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMappingFilter
 * @see MappingContext
 * @see Mapper
 */
@RequiredArgsConstructor
public class PropertyMappingPredicate implements PropertyMappingFilter {
    
    /** 用于判断是否执行映射的BiPredicate，参数为源属性和目标属性 */
    @NonNull
    private final BiPredicate<? super PropertyAccessor, ? super PropertyAccessor> predicate;

    /**
     * 执行属性映射过滤判断
     * <p>
     * 该方法会：
     * <ol>
     *   <li>检查源和目标上下文是否都包含键值</li>
     *   <li>若都包含，则应用谓词判断是否继续映射</li>
     *   <li>若谓词返回true，调用后续映射器继续映射流程</li>
     *   <li>若谓词返回false，直接终止映射并返回false</li>
     * </ol>
     * 
     * @param sourceContext 源映射上下文，包含源属性信息
     * @param targetContext 目标映射上下文，包含目标属性信息
     * @param mapper 用于执行实际映射的映射器
     * @return 若继续映射返回true，否则返回false
     * @throws NullPointerException 若任何参数为null
     */
    @Override
    public boolean doMapping(
            @NonNull MappingContext<Object, PropertyAccessor, TypedProperties> sourceContext,
            @NonNull MappingContext<Object, PropertyAccessor, TypedProperties> targetContext,
            @NonNull Mapper<Object, PropertyAccessor, TypedProperties> mapper) {
        // 检查上下文是否包含键值（即是否有可映射的属性）
        if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
            // 获取源属性和目标属性
            PropertyAccessor sourceProperty = sourceContext.getKeyValue().getValue();
            PropertyAccessor targetProperty = targetContext.getKeyValue().getValue();
            
            // 应用谓词条件，若不满足则终止映射
            if (!predicate.test(sourceProperty, targetProperty)) {
                return false;
            }
        }
        // 继续执行后续映射流程
        return mapper.doMapping(sourceContext, targetContext);
    }
}