package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.spi.ConfigurableServices;

/**
 * 转换器集合类，继承自可配置服务容器，实现统一的类型转换功能。
 * <p>
 * 该类管理多个{@link Converter}实例，通过遍历注册的转换器寻找匹配的类型转换实现，
 * 支持动态配置和扩展转换器，适用于需要多种类型转换策略的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>链式转换：按注册顺序遍历转换器，找到第一个匹配的转换实现</li>
 *   <li>可配置性：继承自{@link ConfigurableServices}，支持动态注册转换器</li>
 *   <li>排序支持：通过{@link ConverterComparator}对转换器进行优先级排序</li>
 *   <li>异常处理：当无匹配转换器时抛出{@link ConverterNotFoundException}</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Converter
 * @see ConfigurableServices
 */
public class Converters extends ConfigurableServices<Converter> implements Converter {

    /**
     * 构造函数，初始化转换器集合并设置默认比较器
     * <p>
     * 使用{@link ConverterComparator#DEFAULT}对转换器进行排序，
     * 确保转换器按优先级顺序尝试转换。
     */
    public Converters() {
        setComparator(ConverterComparator.DEFAULT);
    }

    /**
     * 执行类型转换操作
     * <p>
     * 按注册顺序遍历所有转换器，找到能处理当前源类型和目标类型的转换器，
     * 调用其转换方法并返回结果。若没有匹配的转换器，抛出{@link ConverterNotFoundException}。
     * 
     * @param source                  待转换的源对象
     * @param sourceTypeDescriptor    源类型描述符，不可为null
     * @param targetTypeDescriptor    目标类型描述符，不可为null
     * @return 转换后的目标对象
     * @throws ConversionException          转换过程中发生异常
     * @throws ConverterNotFoundException   未找到匹配的转换器
     * @throws NullPointerException         若类型描述符为null
     */
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
                          @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        for (Converter converter : this) {
            if (converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
                return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
            }
        }
        throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 判断是否存在匹配的转换器
     * <p>
     * 检查是否有任何转换器能处理从源类型到目标类型的转换，
     * 适用于预先判断转换可行性的场景。
     * 
     * @param sourceTypeDescriptor    源类型描述符，不可为null
     * @param targetTypeDescriptor    目标类型描述符，不可为null
     * @return true表示存在匹配的转换器，false表示不存在
     * @throws NullPointerException 若类型描述符为null
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
                             @NonNull TypeDescriptor targetTypeDescriptor) {
        return anyMatch((e) -> e.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
    }
}