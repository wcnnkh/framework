package run.soeasy.framework.core.convert.value;

import java.util.function.Supplier;

import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 类型化数据接口，封装带类型描述的数据，继承自{@link SourceDescriptor}和{@link Supplier<T>}。
 * <p>
 * 该接口用于将数据与其类型描述符绑定，提供类型安全的数据访问方式，
 * 适用于需要明确数据类型信息的场景，如类型转换、数据序列化、参数绑定等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型描述：通过{@link SourceDescriptor}获取数据的类型元信息</li>
 *   <li>数据提供：通过{@link Supplier<T>}获取实际数据值</li>
 *   <li>值包装：通过{@link #value()}方法生成{@link TypedValue}实例</li>
 *   <li>工厂方法：提供静态工厂方法快速创建类型化数据实例</li>
 * </ul>
 *
 * @param <T> 数据的类型
 * 
 * @author soeasy.run
 * @see SourceDescriptor
 * @see Supplier
 * @see TypedValue
 */
public interface TypedData<T> extends SourceDescriptor, Supplier<T> {

    /**
     * 将当前类型化数据转换为{@link TypedValue}实例
     * <p>
     * 创建并返回一个{@link TypedValue}实例，该实例包含：
     * <ol>
     *   <li>通过{@link #get()}获取的数据值</li>
     *   <li>通过{@link #getReturnTypeDescriptor()}获取的类型描述符</li>
     * </ol>
     * 
     * @return 封装了当前数据和类型的TypedValue实例
     */
    default TypedValue value() {
        CustomizeTypedValueAccessor typedValueAccessor = new CustomizeTypedValueAccessor();
        typedValueAccessor.set(get());
        typedValueAccessor.setTypeDescriptor(getReturnTypeDescriptor());
        return typedValueAccessor;
    }

    /**
     * 创建包含指定值的类型化数据实例（类型自动推导）
     * <p>
     * 自动推导数据类型，若需要显式指定类型描述符，可使用{@link #forValue(Object, TypeDescriptor)}。
     * 
     * @param <V>    数据类型
     * @param value  数据值
     * @return 类型化数据实例
     */
    public static <V> TypedData<V> forValue(V value) {
        return forValue(value, null);
    }

    /**
     * 创建包含指定值和类型描述符的类型化数据实例
     * <p>
     * 允许显式指定数据的类型描述符，适用于类型自动推导不准确的场景，
     * 如泛型类型、数组类型或需要明确类型上下文的场景。
     * 
     * @param <V>               数据类型
     * @param value             数据值
     * @param typeDescriptor    类型描述符（可为null，此时自动推导类型）
     * @return 类型化数据实例
     */
    public static <V> TypedData<V> forValue(V value, TypeDescriptor typeDescriptor) {
        CustomizeTypedDataAccessor<V> dataAccessor = new CustomizeTypedDataAccessor<>();
        dataAccessor.set(value);
        dataAccessor.setTypeDescriptor(typeDescriptor);
        return dataAccessor;
    }
}