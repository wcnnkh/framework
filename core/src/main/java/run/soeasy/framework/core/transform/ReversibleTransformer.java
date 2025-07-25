package run.soeasy.framework.core.transform;

import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 双向条件转换器接口，支持源类型(S)与目标类型(T)之间的双向属性转换，继承自{@link ConditionalTransformer}。
 * <p>
 * 该接口通过泛型参数定义双向转换的类型关系，自动生成正向(S-&gt;T)和反向(T-&gt;S)的类型映射，
 * 实现类只需提供{@link #to}和{@link #from}方法的具体转换逻辑，适用于需要双向属性映射的场景，
 * 如DTO与Entity的双向转换、参数与响应的双向映射等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换：自动支持S-&gt;T和T-&gt;S两种转换方向</li>
 *   <li>类型映射：基于泛型参数自动生成双向类型映射关系</li>
 *   <li>条件判断：继承自{@link ConditionalTransformer}，支持基于类型映射的条件转换</li>
 *   <li>泛型解析：通过{@link ResolvableType}自动解析当前接口的泛型参数</li>
 * </ul>
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * 
 * @author soeasy.run
 * @see ConditionalTransformer
 * @see TypeMapping
 * @see ResolvableType
 */
public interface ReversibleTransformer<S, T> extends ConditionalTransformer {

    /**
     * 自动生成当前泛型参数的类型映射关系
     * <p>
     * 通过{@link ResolvableType}解析当前接口的泛型参数S和T，
     * 生成从S到T的类型映射。该方法依赖运行时泛型信息，
     * 若泛型参数在运行时被擦除（如匿名内部类），可能解析失败。
     * 
     * @return 包含S-&gt;T映射关系的TypeMapping实例
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
     * 获取支持的双向类型映射集合
     * <p>
     * 包含正向映射(S-&gt;T)和通过{@link TypeMapping#reversed()}生成的反向映射(T-&gt;S)，
     * 实现类可重写此方法以添加额外的类型映射规则。
     * 
     * @return 包含双向映射的不可变集合（实际为HashSet，建议实现类返回不可变视图）
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
     * 执行双向转换的核心方法
     * <p>
     * 自动判断转换方向：
     * <ol>
     *   <li>若源类型可转换为目标类型，调用{@link #to}方法</li>
     *   <li>若目标类型可转换为源类型，调用{@link #from}方法</li>
     * </ol>
     * 注意：强制类型转换可能引发{@link ClassCastException}，
     * 实现类需确保传入对象的类型与泛型参数一致。
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换成功返回true，否则false
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
     * 正向转换方法：S类型对象转换到T类型对象
     * <p>
     * 由实现类提供具体的属性传输逻辑，如字段映射、类型转换等。
     * 该方法在{@link #transform}检测到正向转换时调用。
     * 
     * @param source S类型源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param target T类型目标对象
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换成功返回true
     */
    boolean to(S source, TypeDescriptor sourceTypeDescriptor, T target, TypeDescriptor targetTypeDescriptor);

    /**
     * 反向转换方法：T类型对象转换到S类型对象
     * <p>
     * 由实现类提供具体的属性传输逻辑，通常为{@link #to}的反向操作，
     * 但实现类可根据业务需求自定义反向转换逻辑。
     * 该方法在{@link #transform}检测到反向转换时调用。
     * 
     * @param source T类型源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param target S类型目标对象
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换成功返回true
     */
    boolean from(T source, TypeDescriptor sourceTypeDescriptor, S target, TypeDescriptor targetTypeDescriptor);
}