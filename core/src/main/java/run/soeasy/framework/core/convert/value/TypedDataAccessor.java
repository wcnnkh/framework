package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 类型化数据访问器接口，整合数据读写能力与类型描述功能，继承自{@link TypedData}和{@link AccessibleDescriptor}。
 * <p>
 * 该接口在{@link TypedData}基础上添加数据写入能力，允许通过类型安全的方式获取和设置数据值，
 * 同时通过{@link AccessibleDescriptor}提供类型描述和访问权限控制，
 * 适用于需要双向数据操作和类型校验的场景，如数据绑定、类型转换上下文等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向数据操作：支持通过{@link #get()}读取数据和{@link #set(Object)}写入数据</li>
 *   <li>类型安全保障：通过{@link TypedData}获取完整的类型描述符{@link TypeDescriptor}</li>
 *   <li>访问权限控制：通过{@link AccessibleDescriptor}判断数据位置的可读/可写状态</li>
 *   <li>接口整合：统一数据访问、类型描述和权限控制的接口契约</li>
 * </ul>
 *
 * @param <T> 数据的类型
 * 
 * @author soeasy.run
 * @see TypedData
 * @see AccessibleDescriptor
 * @see TypeDescriptor
 */
public interface TypedDataAccessor<T> extends TypedData<T>, AccessibleDescriptor {

    /**
     * 设置数据值
     * <p>
     * 允许通过类型安全的方式设置数据值，实现类需确保设置的值与{@link #getReturnTypeDescriptor()}
     * 返回的类型描述符兼容，否则可能导致类型安全问题。
     * 
     * @param value 要设置的数据值，类型需与当前类型描述符兼容
     */
    void set(T value);
}