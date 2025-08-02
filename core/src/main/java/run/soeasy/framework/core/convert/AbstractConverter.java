package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 抽象转换器基类，实现{@link Converter}和{@link ConverterAware}接口，
 * 为具体转换器提供基础功能和默认实现。
 *
 * <p>核心特性：
 * <ul>
 *   <li>委派机制：通过设置的委派转换器处理实际转换逻辑</li>
 *   <li>类型检查：强制子类实现类型兼容性检查</li>
 *   <li>方法聚合：统一实现Converter接口的多种重载方法</li>
 * </ul>
 *
 * <p>使用方式：
 * <ol>
 *   <li>继承此类并实现{@link #canConvert(TypeDescriptor, TypeDescriptor)}方法</li>
 *   <li>通过{@link #setConverter(Converter)}设置委派转换器</li>
 *   <li>或在子类中重写{@link #convert(Object, TypeDescriptor, TypeDescriptor)}方法</li>
 * </ol>
 *
 * @author soeasy.run
 * @see Converter
 * @see ConverterAware
 * @see TypeDescriptor
 */
@Getter
@Setter
public abstract class AbstractConverter implements Converter, ConverterAware {

    /**
     * 委派转换器，用于处理实际的转换逻辑
     * 默认使用{@link Converter#assignable()}转换器
     */
    @NonNull
    private Converter converter = Converter.assignable();

    /**
     * 判断源类型是否可转换为目标类型
     * 子类必须实现此方法以定义自己的类型转换规则
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    public abstract boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor);

    /**
     * 转换对象到指定类型（基于目标Class）
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param <T> 目标类型
     * @param source 源对象，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 转换后的对象
     * @throws ConversionException 转换失败时抛出
     */
    @Override
    public final <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass)
            throws ConversionException {
        return Converter.super.convert(source, targetClass);
    }

    /**
     * 转换对象到指定类型（基于源Class和目标TypeDescriptor）
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param source 源对象
     * @param sourceClass 源类型Class，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的对象
     * @throws ConversionException 转换失败时抛出
     */
    @Override
    public final Object convert(Object source, @NonNull Class<?> sourceClass,
            @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
    }

    /**
     * 转换对象到指定类型（基于源TypeDescriptor和目标Class）
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param <T> 目标类型
     * @param source 源对象
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return 转换后的对象
     * @throws ConversionException 转换失败时抛出
     */
    @Override
    public final <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Class<? extends T> targetClass) throws ConversionException {
        return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
    }

    /**
     * 转换对象到指定类型（基于目标TypeDescriptor）
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param source 源对象
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的对象
     * @throws ConversionException 转换失败时抛出
     */
    @Override
    public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return Converter.super.convert(source, targetTypeDescriptor);
    }

    /**
     * 判断基于Class的类型转换是否可行
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param sourceClass 源类型Class，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return true表示可转换，false表示不可转换
     */
    @Override
    public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
        return Converter.super.canConvert(sourceClass, targetClass);
    }

    /**
     * 判断基于Class和TypeDescriptor的类型转换是否可行
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param sourceClass 源类型Class，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    @Override
    public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
        return Converter.super.canConvert(sourceClass, targetTypeDescriptor);
    }

    /**
     * 判断基于TypeDescriptor和Class的类型转换是否可行
     * 此方法为最终实现，调用委派转换器的相应方法
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetClass 目标类型Class，不可为null
     * @return true表示可转换，false表示不可转换
     */
    @Override
    public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
        return Converter.super.canConvert(sourceTypeDescriptor, targetClass);
    }
}