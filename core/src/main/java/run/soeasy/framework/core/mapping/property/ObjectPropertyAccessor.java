package run.soeasy.framework.core.mapping.property;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 对象属性访问器，实现{@link PropertyAccessor}和{@link PropertyWrapper}接口，
 * 用于访问和操作目标对象的特定属性，支持属性值的读取和写入。
 * <p>
 * 该类采用装饰器模式包装属性描述符（{@code T}），并绑定到特定目标对象，
 * 使属性描述符能够具体操作目标对象的属性值，实现属性访问的解耦和复用。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>属性绑定：将属性描述符与目标对象绑定，实现属性值的具体操作</li>
 *   <li>读写分离：通过{@link #get()}和{@link #set(Object)}方法分别实现属性值的读取和写入</li>
 *   <li>装饰器模式：继承{@link PropertyWrapper}，可在不修改原属性的前提下扩展功能</li>
 *   <li>类型安全：通过泛型约束保证属性描述符的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code T}：属性描述符类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射操作：通过属性描述符动态访问和修改对象属性</li>
 *   <li>数据绑定：将外部数据（如配置、表单）映射到对象属性</li>
 *   <li>属性代理：实现属性访问的拦截和增强（如日志记录、权限控制）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyAccessor
 * @see PropertyWrapper
 * @see Property
 */
@Getter
@RequiredArgsConstructor
public class ObjectPropertyAccessor<T extends Property> implements PropertyAccessor, PropertyWrapper<T> {
    
    /** 被包装的源属性描述符，定义属性的元数据和操作方法 */
    @NonNull
    private final T source;
    
    /** 目标对象，属性访问器将操作该对象的属性值 */
    private final Object target;

    /**
     * 读取目标对象的属性值
     * <p>
     * 该方法调用源属性的{@link Property#readFrom(Object)}方法，
     * 从绑定的目标对象中读取属性值。
     * 
     * @return 属性值，若属性不可读或值为null则返回null
     */
    @Override
    public Object get() {
        return source.readFrom(target);
    }

    /**
     * 设置目标对象的属性值
     * <p>
     * 该方法调用源属性的{@link Property#writeTo(Object, Object)}方法，
     * 将值写入绑定的目标对象的对应属性。
     * 
     * @param value 要设置的属性值
     * @throws UnsupportedOperationException 若属性不可写
     */
    @Override
    public void set(Object value) {
        source.writeTo(target, value);
    }
}