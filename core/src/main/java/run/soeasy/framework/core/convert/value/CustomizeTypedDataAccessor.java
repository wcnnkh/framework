package run.soeasy.framework.core.convert.value;

import lombok.Data;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 自定义类型化数据访问器，实现{@link TypedDataAccessor}接口，用于类型安全地访问和操作数据。
 * <p>
 * 该类提供了对泛型类型数据的访问能力，支持获取和设置值，同时维护类型描述符信息。
 * 当类型描述符未显式设置时，会根据当前值动态推断类型。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全：通过泛型参数确保操作数据的类型一致性</li>
 *   <li>动态类型推断：当未设置类型描述符时，自动根据值推断类型</li>
 *   <li>值访问控制：实现了数据的读写操作接口</li>
 *   <li>值包装转换：可将当前数据转换为{@link TypedValue}实例</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>线程安全：非线程安全实现，不适用于多线程环境</li>
 *   <li>类型描述符：若值为null且未显式设置类型描述符，可能导致类型推断错误</li>
 *   <li>空值处理：未对设置null值进行特殊处理，可能影响后续类型推断</li>
 *   <li>不可变类型：对于不可变类型，set操作可能导致数据不一致</li>
 * </ul>
 * </p>
 *
 * @param <T> 数据的泛型类型
 * 
 * @author soeasy.run
 * @see TypedDataAccessor
 * @see TypeDescriptor
 */
@Data
public class CustomizeTypedDataAccessor<T> implements TypedDataAccessor<T> {
    
    /** 存储的数据值 */
    private T value;
    
    /** 类型描述符，若未设置则根据value动态推断 */
    private TypeDescriptor typeDescriptor;

    /**
     * 获取返回类型描述符（实现接口方法）
     * <p>
     * 若类型描述符已显式设置，则返回该描述符；
     * 否则根据当前值动态生成类型描述符。
     * 
     * @return 类型描述符
     */
    @Override
    public final TypeDescriptor getReturnTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 获取类型描述符
     * <p>
     * 若已设置类型描述符，则直接返回；
     * 若未设置且值不为null，则根据值推断类型；
     * 若未设置且值为null，则返回null（需注意可能导致的类型安全问题）。
     * 
     * @return 类型描述符
     */
    public TypeDescriptor getTypeDescriptor() {
        return typeDescriptor == null ? TypeDescriptor.forObject(value) : typeDescriptor;
    }

    /**
     * 获取存储的值
     * 
     * @return 当前存储的值
     */
    @Override
    public T get() {
        return value;
    }

    /**
     * 获取所需的类型描述符（实现接口方法）
     * <p>
     * 与{@link #getReturnTypeDescriptor()}返回相同的类型描述符。
     * 
     * @return 类型描述符
     */
    @Override
    public final TypeDescriptor getRequiredTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 设置存储的值
     * <p>
     * 注意：设置null值可能影响后续类型推断，
     * 若需保持类型描述符，请先显式设置typeDescriptor。
     * 
     * @param value 要设置的值
     */
    @Override
    public void set(T value) {
        this.value = value;
    }

    /**
     * 将当前数据访问器转换为类型化值访问器
     * <p>
     * 创建一个新的{@link CustomizeTypedValueAccessor}实例，
     * 并将当前的值和类型描述符复制到新实例中。
     * 
     * @return 类型化值访问器实例
     */
    @Override
    public TypedValue value() {
        CustomizeTypedValueAccessor valueAccessor = new CustomizeTypedValueAccessor();
        valueAccessor.setValue(this.value);
        valueAccessor.setTypeDescriptor(this.typeDescriptor);
        return valueAccessor;
    }
}