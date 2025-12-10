package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.mapping.property.PropertyMappingWrapper;

/**
 * 自定义注解属性映射器，继承自{@link AbstractAnnotationPropertyMapping}，
 * 实现{@link AnnotationProperties}和{@link PropertyMappingWrapper}接口，
 * 用于将任意类型的属性包装为注解属性，支持自定义注解类型与属性的映射关系。
 * <p>
 * 该类通过组合模式将外部属性源与注解类型绑定，实现注解属性的动态访问，
 * 适用于需要将现有属性结构转换为注解表示的场景，如配置项映射、元数据转换等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>属性包装：将任意{@link PropertyMapping}实现包装为注解属性</li>
 *   <li>类型安全：通过泛型约束确保注解类型与属性的一致性</li>
 *   <li>自定义映射：支持自定义注解类型与属性源的映射关系</li>
 *   <li>接口适配：实现TypedPropertiesWrapper接口，保持与属性体系的兼容性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>A：目标注解类型，必须是{@link Annotation}的子类</li>
 *   <li>P：源属性类型，必须是{@link PropertyMapping}的子类</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>配置项映射：将配置属性映射为注解形式</li>
 *   <li>元数据转换：将外部元数据转换为注解表示</li>
 *   <li>测试框架：动态构造测试所需的注解</li>
 *   <li>注解代理：为现有属性提供注解式访问接口</li>
 *   <li>插件系统：将插件配置转换为框架可识别的注解</li>
 * </ul>
 *
 * @author soeasy.run
 * @see AnnotationProperties
 * @see TypedPropertiesWrapper
 * @see AbstractAnnotationPropertyMapping
 */
@Getter
@RequiredArgsConstructor
public class CustomizeAnnotationPropertyMapping<A extends Annotation, P extends PropertyMapping<PropertyAccessor>>
        extends AbstractAnnotationPropertyMapping<A> implements AnnotationProperties<A>, PropertyMappingWrapper<PropertyAccessor, P> {
    /**
     * 目标注解类型
     */
    @NonNull
    private final Class<A> type;
    
    /**
     * 源属性对象
     */
    @NonNull
    private final P source;
    
    
}