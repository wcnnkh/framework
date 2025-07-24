package run.soeasy.framework.core.convert;

import lombok.NonNull;

/**
 * 抽象条件型转换器基类，继承自{@link AbstractConverter}并实现{@link ConditionalConverter}接口。
 * 该基类为条件型转换器提供统一的类型转换可行性判断逻辑，
 * 子类只需关注具体的类型映射集合定义。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型映射集合支持：继承自{@link ConditionalConverter}的类型映射机制</li>
 *   <li>委派转换逻辑：复用{@link AbstractConverter}的转换器委派机制</li>
 *   <li>最终方法保护：关键方法已被final修饰，确保类型判断逻辑一致性</li>
 * </ul>
 *
 * <p>使用方式：
 * <ol>
 *   <li>继承此类并实现{@link #getConvertibleTypeMappings()}方法</li>
 *   <li>通过类型映射集合定义可转换的源类型与目标类型组合</li>
 *   <li>可通过{@link #setConverter(Converter)}设置后备转换器</li>
 * </ol>
 *
 * @author soeasy.run
 * @see ConditionalConverter
 * @see AbstractConverter
 * @see TypeMapping
 */
public abstract class AbstractConditionalConverter extends AbstractConverter implements ConditionalConverter {

    /**
     * 判断源类型是否可转换为目标类型
     * 此实现直接调用{@link ConditionalConverter}接口的默认实现，
     * 基于类型映射集合进行判断。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
    }
}