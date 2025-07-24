package run.soeasy.framework.core.convert;

import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 可逆转换器接口，支持源类型S与目标类型T之间的双向转换，自动推导类型映射关系。
 * <p>
 * 该接口继承自{@link ConditionalConverter}，在条件转换基础上增加双向转换能力，
 * 适用于需要支持类型往返转换的场景，如对象序列化/反序列化、数据格式转换等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换：支持S→T和T→S两个方向的类型转换</li>
 *   <li>类型推导：基于泛型参数自动解析源类型和目标类型</li>
 *   <li>智能路由：自动判断转换方向并选择对应转换方法</li>
 *   <li>多级转换：支持通过中间类型实现复杂转换路径</li>
 * </ul>
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * 
 * @author soeasy.run
 * @see ConditionalConverter
 * @see TypeMapping
 */
public interface ReversibleConverter<S, T> extends ConditionalConverter {

    /**
     * 获取基于泛型参数推导的类型映射关系
     * <p>
     * 通过解析当前接口的泛型类型参数，自动推导出源类型和目标类型，
     * 生成对应的{@link TypeMapping}实例，用于描述S→T的类型转换关系。
     * 
     * @return 包含S→T映射关系的类型映射对象
     */
    default TypeMapping getTypeMapping() {
        // 解析当前接口实现类的泛型类型参数
        ResolvableType resolvableType = ResolvableType.forType(getClass());
        resolvableType = resolvableType.as(ReversibleConverter.class);
        // 获取泛型参数的原始类型
        Class<?> sourceType = resolvableType.getActualTypeArgument(0).getRawType();
        Class<?> targetType = resolvableType.getActualTypeArgument(1).getRawType();

        return new TypeMapping(sourceType, targetType);
    }

    /**
     * 获取所有可转换的类型映射集合
     * <p>
     * 包含正向映射(S→T)和反向映射(T→S)，确保双向转换能力，
     * 返回的集合始终包含两个{@link TypeMapping}实例。
     * 
     * @return 包含正向和反向类型映射的不可变集合
     */
    @Override
    default Set<TypeMapping> getConvertibleTypeMappings() {
        TypeMapping typeMapping = getTypeMapping();
        Set<TypeMapping> typeMappings = new HashSet<>(2, 1);
        typeMappings.add(typeMapping);        // 正向映射(S→T)
        typeMappings.add(typeMapping.reversed()); // 反向映射(T→S)
        return typeMappings;
    }

    /**
     * 判断是否支持指定类型的转换
     * <p>
     * 先检查是否符合当前转换器的双向映射关系，若不符合则检查是否可直接赋值，
     * 确保类型转换的可行性判断全面准确。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     * @throws NullPointerException 若类型描述符为null
     */
    @Override
    default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
                              @NonNull TypeDescriptor targetTypeDescriptor) {
        return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
                || Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行类型转换操作（自动选择转换方向）
     * <p>
     * 转换逻辑流程：
     * <ol>
     *   <li>检查是否为S→T的直接转换</li>
     *   <li>检查是否为T→S的反向转换</li>
     *   <li>检查是否可直接赋值（无需转换）</li>
     *   <li>通过中间类型实现复杂转换（S→T→目标类型）</li>
     * </ol>
     * 
     * @param source               待转换的源对象
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的目标对象
     * @throws ConversionException 当无法完成转换时抛出
     * @throws NullPointerException 若类型描述符为null
     */
    @SuppressWarnings("unchecked")
    @Override
    default Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
                          @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        TypeMapping typeMapping = getTypeMapping();
        // 检查是否为S->T的正向转换
        if (typeMapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
            return to((S) source, sourceTypeDescriptor, targetTypeDescriptor);
        }
        // 检查是否为T->S的反向转换
        else if (typeMapping.canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
            return from((T) source, sourceTypeDescriptor, targetTypeDescriptor);
        }

        // 检查是否可直接赋值（无需转换）
        if (Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
            return source;
        }

        // 复杂转换：通过中间类型(T)实现S->中间类型->目标类型
        TypeDescriptor contentType = new TypeDescriptor(
                ResolvableType.forType(typeMapping.getValue()),
                typeMapping.getValue(),
                getClass()
        );
        T target = typeMapping.getValue().isInstance(source) 
                ? (T) source 
                : to((S) source, sourceTypeDescriptor, contentType);
        return from(target, contentType, targetTypeDescriptor);
    }

    /**
     * 执行S→T方向的类型转换
     * <p>
     * 由实现类提供具体的转换逻辑，将源类型S的对象转换为目标类型T。
     * 
     * @param source               源对象（S类型）
     * @param sourceTypeDescriptor 源类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换后的T类型对象
     * @throws ConversionException 转换过程中发生错误时抛出
     */
    T to(S source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) throws ConversionException;

    /**
     * 执行T→S方向的类型转换
     * <p>
     * 由实现类提供具体的转换逻辑，将源类型T的对象转换为目标类型S。
     * 
     * @param source               源对象（T类型）
     * @param sourceTypeDescriptor 源类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换后的S类型对象
     * @throws ConversionException 转换过程中发生错误时抛出
     */
    S from(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) throws ConversionException;
}