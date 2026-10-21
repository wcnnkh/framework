package run.soeasy.framework.beans;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.execute.reflect.ReflectionProperty;

/**
 * 封装JavaBean属性信息的类，继承自{@link ReflectionProperty}，结合{@link PropertyDescriptor}提供Bean属性的元数据（如读写方法、属性名等），
 * 用于描述和操作JavaBean的属性，适配反射场景下的属性访问与操作。
 * 
 * <p>该类通过{@link PropertyDescriptor}获取属性的读写方法（getter/setter），并维护属性名称与所属Bean类的关联，
 * 支持动态更新属性描述符，确保属性元数据的准确性。
 * 
 * @author soeasy.run
 * @see ReflectionProperty
 * @see PropertyDescriptor
 */
@Getter
public class BeanProperty extends ReflectionProperty implements Serializable {

    /**
     * 序列化版本号，用于确保序列化与反序列化的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * Bean属性描述符，存储属性的元数据（如属性名、读写方法等），
     * transient修饰表示不参与序列化，volatile保证多线程环境下的可见性
     */
    private transient volatile PropertyDescriptor propertyDescriptor;

    /**
     * 构造Bean属性实例（基于Bean类和属性描述符）
     * 
     * @param beanClass 属性所属的Bean类（非空）
     * @param propertyDescriptor 属性描述符（非空，包含属性的元数据信息）
     */
    public BeanProperty(@NonNull Class<?> beanClass, @NonNull PropertyDescriptor propertyDescriptor) {
        super(beanClass, propertyDescriptor.getName());
        setPropertyDescriptor(propertyDescriptor);
    }

    /**
     * 同步设置属性描述符，并更新属性相关信息（线程安全）
     * 
     * <p>该方法会更新属性名称、读方法（getter）和写方法（setter），确保属性元数据与描述符保持一致。
     * 
     * @param propertyDescriptor 新的属性描述符（非空）
     */
    public synchronized void setPropertyDescriptor(@NonNull PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
        this.setName(propertyDescriptor.getName());
        // 设置读方法（getter）
        if (propertyDescriptor.getReadMethod() != null) {
            setReadMethod(propertyDescriptor.getReadMethod());
        }
        // 设置写方法（setter）
        if (propertyDescriptor.getWriteMethod() != null) {
            setWriteMethod(propertyDescriptor.getWriteMethod());
        }
    }
}