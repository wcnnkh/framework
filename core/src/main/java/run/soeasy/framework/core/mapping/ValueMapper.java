package run.soeasy.framework.core.mapping;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.streaming.Mapping;

/**
 * 值映射器，实现{@link Mapper}接口，负责将源值转换并映射到目标位置。
 * <p>
 * 该映射器使用{@link Converter}进行类型转换，支持在两个映射上下文之间进行值的转换和设置。
 * 映射过程遵循以下步骤：
 * <ol>
 *   <li>验证源和目标上下文是否存在键值对</li>
 *   <li>检查源值是否可读且目标值是否可写</li>
 *   <li>确认转换器是否支持源类型到目标类型的转换</li>
 *   <li>执行类型转换并设置目标值</li>
 * </ol>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型转换：通过可配置的转换器支持多种类型间的转换</li>
 *   <li>空值处理：可配置是否允许空值映射</li>
 *   <li>条件验证：在映射前进行多重条件检查，确保映射安全</li>
 *   <li>链式调用：支持通过setter方法进行流式配置</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * ValueMapper<String, MyValue, MyMapping> mapper = new ValueMapper<>();
 * mapper.setConverter(myCustomConverter);
 * boolean result = mapper.doMapping(sourceContext, targetContext);
 * }</pre>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping}
 * 
 * @author soeasy.run
 * @see Mapper
 * @see Converter
 * @see MappingContext
 * @see TypedValueAccessor
 */
@Getter
@Setter
public class ValueMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
    
    /** 用于类型转换的转换器，默认为可分配类型转换器 */
    @NonNull
    private Converter converter = Converter.assignable();

    /**
     * 执行值的映射转换
     * <p>
     * 该方法从源上下文中读取值，通过转换器进行类型转换，然后将结果写入目标上下文。
     * 映射成功需满足以下条件：
     * <ul>
     *   <li>源和目标上下文均存在键值对</li>
     *   <li>源值可读且目标值可写</li>
     *   <li>转换器支持源类型到目标类型的转换</li>
     *   <li>转换结果非空（若目标值为必需项）</li>
     * </ul>
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @return 映射成功返回true，否则false
     */
    @Override
    public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
                             @NonNull MappingContext<K, V, T> targetContext) {
        // 验证上下文是否存在可用的键值对
        if (!(sourceContext.hasKeyValue() && targetContext.hasKeyValue())) {
            return false;
        }

        // 获取源值访问器和目标值访问器
        TypedValueAccessor sourceAccessor = sourceContext.getKeyValue().getValue();
        TypedValueAccessor targetAccessor = targetContext.getKeyValue().getValue();
        
        // 验证访问权限
        if (!(sourceAccessor.isReadable() && targetAccessor.isWriteable())) {
            return false;
        }

        // 验证转换器是否支持该类型转换
        if (!converter.canConvert(sourceAccessor.getReturnTypeDescriptor(),
                                  targetAccessor.getRequiredTypeDescriptor())) {
            return false;
        }

        // 执行类型转换
        Object value = converter.convert(sourceAccessor.get(), 
                                        sourceAccessor.getReturnTypeDescriptor(),
                                        targetAccessor.getRequiredTypeDescriptor());
        
        // 处理必需值的空值情况
        if (value == null && targetAccessor.isRequired()) {
            return false;
        }
        
        // 设置转换后的值
        targetAccessor.set(value);
        return true;
    }

    /**
     * 设置自定义转换器
     * 
     * @param converter 自定义转换器，不可为null
     * @return 当前映射器实例，支持链式调用
     */
    public ValueMapper<K, V, T> withConverter(@NonNull Converter converter) {
        this.converter = converter;
        return this;
    }
}