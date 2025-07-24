package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 类型化值访问器接口，整合类型化值操作与数据访问能力，支持类型安全的读写和转换。
 * <p>
 * 该接口继承自{@link TypedValue}和{@link TypedDataAccessor}，融合了：
 * <ul>
 *   <li>{@link TypedValue}的类型化值转换能力</li>
 *   <li>{@link TypedDataAccessor}的数据读写能力</li>
 * </ul>
 * 适用于需要双向数据操作和类型转换的场景，如数据绑定、表单处理、配置解析等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向数据操作：支持通过{@link #get()}读取和{@link TypedDataAccessor#set(Object)}写入数据</li>
 *   <li>类型安全转换：通过{@link #map}方法支持类型转换的链式调用</li>
 *   <li>访问权限控制：继承自{@link AccessibleDescriptor}，支持读写权限判断</li>
 *   <li>空值安全：所有转换方法均处理空值情况，避免NPE</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypedValue
 * @see TypedDataAccessor
 * @see MappedTypedValueAccessor
 */
public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {

    /**
     * 转换为指定类型的可访问类型化值（泛型方法）
     * <p>
     * 使用指定的转换器将当前值转换为目标类型，返回新的{@link TypedValueAccessor}实例。
     * 转换逻辑：
     * <ol>
     *   <li>根据目标类型Class创建{@link TypeDescriptor}</li>
     *   <li>调用{@link #map(TypeDescriptor, Converter)}执行转换</li>
     *   <li>返回强转后的可访问类型化值</li>
     * </ol>
     * 
     * @param <R>        目标类型
     * @param type       目标类型Class，不可为null
     * @param converter  转换器，不可为null
     * @return 转换后的可访问类型化值实例
     * @throws NullPointerException 若type或converter为null
     */
    @SuppressWarnings("unchecked")
    @Override
    default <R> TypedDataAccessor<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
        return (TypedDataAccessor<R>) map(TypeDescriptor.valueOf(type), converter);
    }

    /**
     * 转换为指定类型的可访问类型化值
     * <p>
     * 使用指定的转换器和类型描述符将当前值转换为目标类型，返回新的{@link TypedValueAccessor}实例。
     * 转换过程中保留原始值的类型信息，并应用转换器进行类型转换，支持链式调用。
     * 
     * @param typeDescriptor 目标类型描述符，不可为null
     * @param converter      转换器，不可为null
     * @return 转换后的可访问类型化值实例
     * @throws NullPointerException 若typeDescriptor或converter为null
     */
    @Override
    default TypedValueAccessor map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
        return new MappedTypedValueAccessor<>(this, typeDescriptor, converter);
    }
}