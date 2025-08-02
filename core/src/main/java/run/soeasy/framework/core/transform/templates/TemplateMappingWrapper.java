package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 模板映射包装器接口，继承自{@link TemplateMapping}、{@link MappingWrapper}和{@link TemplateWrapper}，
 * 用于包装基础模板映射实例并添加额外功能或修改行为，遵循包装器设计模式。
 * <p>
 * 该接口允许将一个模板映射实例包装为具有相同接口的新实例，从而在不修改原模板映射实现的前提下，
 * 实现功能增强（如日志记录、权限控制、数据验证等）。包装器会将所有操作委托给源模板映射实例，
 * 并可在委托前后添加自定义逻辑。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>包装器模式：通过{@link #getSource()}获取被包装的源模板映射实例</li>
 *   <li>透明代理：对外暴露与源模板映射一致的接口，客户端无感知</li>
 *   <li>功能增强：可在委托操作前后添加额外逻辑（如验证、转换等）</li>
 *   <li>集合转换：继承{@link TemplateMapping}的集合转换方法，委托给源模板映射处理</li>
 * </ul>
 *
 * @param <E> 模板映射元素的类型，必须实现{@link TypedValueAccessor}接口
 * @param <W> 包装器自身的类型，需实现{@link TemplateMapping}
 * 
 * @author soeasy.run
 * @see TemplateMapping
 * @see MappingWrapper
 * @see TemplateWrapper
 */
@FunctionalInterface
public interface TemplateMappingWrapper<E extends TypedValueAccessor, W extends TemplateMapping<E>>
        extends TemplateMapping<E>, MappingWrapper<Object, E, W>, TemplateWrapper<E, W> {

    /**
     * 根据键获取元素（委托给源模板映射）
     * <p>
     * 该实现直接调用源模板映射的{@link TemplateMapping#get(Object)}方法，
     * 包装器不做额外处理。
     * 
     * @param key 键对象，可以是数值类型或其他类型
     * @return 对应的唯一元素，若键不存在则返回null
     * @see TemplateMapping#get(Object)
     */
    @Override
    default E get(Object key) {
        // 添加空值检查，增强健壮性
        W source = getSource();
        if (source == null) {
            throw new IllegalStateException("Wrapped template mapping source cannot be null");
        }
        return source.get(key);
    }

    /**
     * 将模板映射转换为Map形式（委托给源模板映射）
     * <p>
     * 该实现直接调用源模板映射的{@link TemplateMapping#asMap(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的模板映射实例
     * @see TemplateMapping#asMap(boolean)
     */
    @Override
    default TemplateMapping<E> asMap(boolean uniqueness) {
        // 添加空值检查，增强健壮性
        W source = getSource();
        if (source == null) {
            throw new IllegalStateException("Wrapped template mapping source cannot be null");
        }
        return source.asMap(uniqueness);
    }

    /**
     * 将模板映射转换为数组形式（委托给源模板映射）
     * <p>
     * 该实现直接调用源模板映射的{@link TemplateMapping#asArray(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的模板映射实例
     * @see TemplateMapping#asArray(boolean)
     */
    @Override
    default TemplateMapping<E> asArray(boolean uniqueness) {
        // 添加空值检查，增强健壮性
        W source = getSource();
        if (source == null) {
            throw new IllegalStateException("Wrapped template mapping source cannot be null");
        }
        return source.asArray(uniqueness);
    }
}