package run.soeasy.framework.core.transform;

import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 双向转换器接口，支持源类型(S)与目标类型(T)之间的双向属性转换。
 * 继承自{@link ConditionalTransformer}，通过类型映射集合实现条件转换判断。
 * 
 * @param <S> 源类型
 * @param <T> 目标类型
 * @see ConditionalTransformer 条件转换器基接口
 * @see TypeMapping 类型映射定义
 */
public interface ReversibleTransformer<S, T> extends ConditionalTransformer {

    /**
     * 自动解析泛型参数生成类型映射关系。
     * 基于接口泛型参数S和T生成源类型到目标类型的映射。
     * 
     * @return TypeMapping实例，包含S-&gt;T的类型映射关系
     * @throws IllegalStateException 当泛型参数解析失败时抛出
     */
    default TypeMapping getTypeMapping() {
        // 通过ResolvableType解析当前接口的泛型参数
        ResolvableType resolvableType = ResolvableType.forType(getClass());
        resolvableType = resolvableType.as(ReversibleTransformer.class);
        
        // 提取泛型参数的原始类型
        Class<?> sourceType = resolvableType.getActualTypeArgument(0).getRawType();
        Class<?> targetType = resolvableType.getActualTypeArgument(1).getRawType();
        
        return new TypeMapping(sourceType, targetType);
    }

    /**
     * 获取支持的双向类型映射集合。
     * 包含正向映射(S-&gt;T)和反向映射(T-&gt;S)。
     * 
     * @return 不可变的TypeMapping集合，包含两个映射关系
     */
    @Override
    default Set<TypeMapping> getTransformableTypeMappings() {
        TypeMapping typeMapping = getTypeMapping();
        Set<TypeMapping> typeMappings = new HashSet<>(2);
        typeMappings.add(typeMapping);         // 正向映射
        typeMappings.add(typeMapping.reversed()); // 反向映射
        return typeMappings;
    }

    /**
     * 执行双向转换的核心方法。
     * 根据源类型和目标类型自动判断调用正向或反向转换逻辑。
     * 
     * @param source 源对象，非null
     * @param sourceTypeDescriptor 源类型描述符，非null
     * @param target 目标对象，非null
     * @param targetTypeDescriptor 目标类型描述符，非null
     * @return 转换成功返回true，否则返回false
     * @throws ClassCastException 当对象类型与泛型参数不匹配时抛出
     */
    @SuppressWarnings("unchecked")
    @Override
    default boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        TypeMapping typeMapping = getTypeMapping();
        
        // 正向转换判断：S->T
        if (typeMapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
            return to((S) source, sourceTypeDescriptor, (T) target, targetTypeDescriptor);
        }
        // 反向转换判断：T->S
        else if (typeMapping.canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
            return from((T) source, sourceTypeDescriptor, (S) target, targetTypeDescriptor);
        }
        return false;
    }

    /**
     * 正向转换方法：S类型对象转换到T类型对象。
     * 由实现类提供具体的属性传输逻辑。
     * 
     * @param source S类型源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param target T类型目标对象
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换成功返回true
     */
    boolean to(S source, TypeDescriptor sourceTypeDescriptor, T target, TypeDescriptor targetTypeDescriptor);

    /**
     * 反向转换方法：T类型对象转换到S类型对象。
     * 由实现类提供具体的属性传输逻辑。
     * 
     * @param source T类型源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param target S类型目标对象
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换成功返回true
     */
    boolean from(T source, TypeDescriptor sourceTypeDescriptor, S target, TypeDescriptor targetTypeDescriptor);
}