package run.soeasy.framework.core.convert;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;

/**
 * 转换器注册表，基于树形映射实现类型到转换器的动态注册与查找，实现{@link ConditionalConverter}接口。
 * <p>
 * 该类继承自{@link TreeMapContainer}，以{@link TypeMapping}为键存储转换器，
 * 支持按源类型和目标类型快速查找匹配的转换器，适用于需要动态管理类型转换策略的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射存储：使用{@link TypeMapping}作为键，支持双向类型转换查找</li>
 *   <li>条件转换：实现{@link ConditionalConverter}接口，支持转换可行性判断</li>
 *   <li>批量注册：支持一次性注册多个类型映射的转换器</li>
 *   <li>函数式注册：提供函数式接口注册方式，简化转换器创建</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ConditionalConverter
 * @see TypeMapping
 * @see FunctionConverter
 */
public class ConverterRegistry extends TreeMapContainer<TypeMapping, Converter> implements ConditionalConverter {

    /**
     * 判断是否存在可处理指定类型转换的转换器
     * <p>
     * 先尝试通过类型映射哈希查找，若未找到则遍历所有注册的转换器进行条件匹配。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示存在匹配转换器，false表示不存在
     * @throws NullPointerException 若类型描述符为null
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
                             @NonNull TypeDescriptor targetTypeDescriptor) {
        return getConverter(sourceTypeDescriptor, targetTypeDescriptor) != null;
    }

    /**
     * 执行类型转换操作
     * <p>
     * 按以下顺序查找转换器：
     * <ol>
     *   <li>通过源类型->目标类型映射查找</li>
     *   <li>通过目标类型->源类型映射反向查找</li>
     *   <li>遍历所有转换器进行条件匹配</li>
     * </ol>
     * 
     * @param source                  待转换对象
     * @param sourceTypeDescriptor    源类型描述符，不可为null
     * @param targetTypeDescriptor    目标类型描述符，不可为null
     * @return 转换后的对象
     * @throws ConversionException          转换过程中发生异常
     * @throws ConverterNotFoundException   未找到匹配转换器
     * @throws NullPointerException         若类型描述符为null
     */
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
                          @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        Converter converter = getConverter(sourceTypeDescriptor, targetTypeDescriptor);
        if (converter == null) {
            throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
        }
        return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 获取匹配的转换器（包含双向查找逻辑）
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 匹配的转换器，未找到返回null
     */
    private Converter getConverter(@NonNull TypeDescriptor sourceTypeDescriptor,
                                  @NonNull TypeDescriptor targetTypeDescriptor) {
        // 先尝试正向类型映射查找
        Converter converter = getConverterByHash(sourceTypeDescriptor, targetTypeDescriptor);
        if (converter == null) {
            // 再尝试反向类型映射查找（目标->源）
            converter = getConverterByHash(targetTypeDescriptor, sourceTypeDescriptor);
        }

        // 最后遍历所有转换器进行条件匹配
        for (Entry<TypeMapping, Converter> entry : entrySet()) {
            if (entry.getValue().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 通过类型映射哈希查找转换器
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 匹配的转换器，未找到返回null
     */
    private Converter getConverterByHash(@NonNull TypeDescriptor sourceTypeDescriptor,
                                        @NonNull TypeDescriptor targetTypeDescriptor) {
        TypeMapping typeMapping = new TypeMapping(sourceTypeDescriptor.getType(), targetTypeDescriptor.getType());
        return get(typeMapping);
    }

    /**
     * 获取所有可转换的类型映射集合
     * 
     * @return 类型映射集合
     */
    @Override
    public Set<TypeMapping> getConvertibleTypeMappings() {
        return keySet();
    }

    /**
     * 注册函数式转换器
     * <p>
     * 封装函数为{@link FunctionConverter}并注册到注册表，支持泛型类型参数。
     * 
     * @param <S>        源类型
     * @param <T>        目标类型
     * @param sourceClass 源类型Class
     * @param targetClass 目标类型Class
     * @param function    转换函数
     * @return 注册句柄，用于取消注册
     * @throws NullPointerException 若参数为null
     */
    public final <S, T> Registration register(@NonNull Class<S> sourceClass, @NonNull Class<T> targetClass,
                                             Function<? super S, ? extends T> function) {
        FunctionConverter<S, T> converter = new FunctionConverter<>(sourceClass, targetClass, function);
        return register(converter);
    }

    /**
     * 注册条件转换器
     * <p>
     * 根据条件转换器提供的类型映射集合，批量注册到注册表，返回组合注册句柄。
     * 
     * @param conditionalConverter 条件转换器，不可为null
     * @return 组合注册句柄，取消时会移除所有相关注册
     * @throws NullPointerException 若转换器为null
     */
    public Registration register(@NonNull ConditionalConverter conditionalConverter) {
        Set<TypeMapping> typeMappings = conditionalConverter.getConvertibleTypeMappings();
        List<Registration> registrations = typeMappings.stream()
                .map((e) -> register(e, conditionalConverter))
                .collect(Collectors.toList());
        return Registrations.forList(registrations);
    }
}