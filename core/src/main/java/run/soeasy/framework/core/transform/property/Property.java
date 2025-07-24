package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor;

/**
 * 属性接口，继承自{@link PropertyDescriptor}，定义对象属性的读写操作规范，
 * 是属性访问和操作的核心接口，支持从目标对象读取属性值和写入属性值。
 * <p>
 * 该接口在{@link PropertyDescriptor}基础上增加了属性值操作能力，
 * 适用于对象属性的反射操作、数据绑定、类型转换等需要动态访问属性值的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>读写能力：通过{@link #isReadable()}和{@link #isWriteable()}判断属性可访问性</li>
 *   <li>值操作：提供{@link #readFrom(Object)}读取属性值和{@link #writeTo(Object, Object)}写入属性值</li>
 *   <li>访问器封装：通过{@link #accessor(Object)}获取属性访问器实例</li>
 *   <li>元数据继承：继承{@link PropertyDescriptor}的属性名称和描述信息</li>
 * </ul>
 * </p>
 *
 * <p><b>实现注意事项：</b>
 * <ul>
 *   <li>读写操作应保证线程安全（若涉及共享对象访问）</li>
 *   <li>属性值转换责任：实现者需处理源值与目标属性的类型适配</li>
 *   <li>异常处理规范：读写操作应抛出明确的运行时异常（如{@link IllegalArgumentException}）</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see PropertyDescriptor
 * @see PropertyAccessor
 * @see ObjectPropertyAccessor
 */
public interface Property extends PropertyDescriptor {

    /**
     * 判断属性是否可读
     * <p>
     * 继承自{@link AccessibleDescriptor}，
     * 返回true表示可以通过{@link #readFrom(Object)}读取属性值
     * </p>
     * 
     * @return 属性可读返回true，否则false
     */
    @Override
    boolean isReadable();

    /**
     * 判断属性是否可写
     * <p>
     * 继承自{@link AccessibleDescriptor}，
     * 返回true表示可以通过{@link #writeTo(Object, Object)}写入属性值
     * </p>
     * 
     * @return 属性可写返回true，否则false
     */
    @Override
    boolean isWriteable();

    /**
     * 从目标对象读取属性值
     * <p>
     * 该方法从指定目标对象中读取当前属性的值，
     * 实现者需处理目标对象为null或属性不存在的情况
     * </p>
     * 
     * @param target 目标对象，可能为null
     * @return 属性值，目标对象为null或属性不可读时返回null
     * @throws RuntimeException 读取过程中发生错误时抛出
     */
    Object readFrom(Object target);

    /**
     * 向目标对象写入属性值
     * <p>
     * 该方法向指定目标对象的当前属性写入值，
     * 实现者需处理值类型与属性类型不匹配的情况
     * </p>
     * 
     * @param target 目标对象，可能为null
     * @param value 要写入的值，可能为null
     * @throws RuntimeException 写入过程中发生错误时抛出
     */
    void writeTo(Object target, Object value);

    /**
     * 获取属性访问器
     * <p>
     * 创建并返回一个{@link PropertyAccessor}实例，
     * 封装当前属性和目标对象的访问能力
     * </p>
     * 
     * @param target 目标对象
     * @return 属性访问器实例
     * @see ObjectPropertyAccessor
     */
    default PropertyAccessor accessor(Object target) {
        return new ObjectPropertyAccessor<>(this, target);
    }
}