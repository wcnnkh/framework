package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 反射字段实现类，实现{@link Property}接口和{@link Serializable}接口，
 * 用于封装Java反射中的Field对象，提供类字段的属性级访问能力。
 * <p>
 * 该类支持通过字段名称动态查找字段，并提供字段值的读写功能，
 * 实现了属性接口以统一字段与方法的访问方式。同时支持序列化，
 * 便于在远程调用或持久化场景中使用。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Field对象封装为可操作的属性</li>
 *   <li>动态查找：支持通过类和字段名称动态查找字段</li>
 *   <li>属性抽象：实现Property接口，统一字段与方法的属性访问方式</li>
 *   <li>序列化支持：实现Serializable接口，支持字段的序列化和反序列化</li>
 *   <li>延迟初始化：使用延迟加载机制查找字段，提高性能</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Bean属性操作：直接访问JavaBean的字段</li>
 *   <li>对象映射：在不同对象间映射字段值</li>
 *   <li>序列化框架：自定义对象的序列化和反序列化</li>
 *   <li>配置管理：动态读取和设置配置字段</li>
 *   <li>测试工具：在测试中访问对象的私有字段</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Property
 * @see java.lang.reflect.Field
 */
public class ReflectionField implements Property, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 字段所属的声明类，用于动态查找字段
     */
    private final Class<?> declaringClass;
    
    /**
     * 字段名称，用于动态查找字段
     */
    private volatile String name;
    
    /**
     * 字段的Supplier函数，用于延迟加载字段对象
     */
    private transient volatile Supplier<Field> fieldSupplier;
    
    /**
     * 字段的类型描述符，使用双重检查锁延迟初始化
     */
    private transient volatile TypeDescriptor typeDescriptor;

    /**
     * 构造函数，通过声明类和字段名称初始化反射字段
     * 
     * @param declaringClass 字段所属的声明类，不可为null
     * @param name 字段名称，不可为null
     */
    public ReflectionField(@NonNull Class<?> declaringClass, @NonNull String name) {
        this.declaringClass = declaringClass;
        this.name = name;
    }

    /**
     * 构造函数，通过已有的Field对象初始化反射字段
     * 
     * @param field 反射Field对象，不可为null
     */
    public ReflectionField(@NonNull Field field) {
        this(field.getDeclaringClass(), field.getName());
        this.fieldSupplier = () -> field;
    }

    /**
     * 设置反射Field对象
     * <p>
     * 该方法会更新字段供应商，并清空缓存的类型描述符
     * </p>
     * 
     * @param field 反射Field对象，不可为null
     */
    public synchronized void setField(@NonNull Field field) {
        this.fieldSupplier = () -> field;
        this.name = field.getName();
        this.typeDescriptor = null;
    }

    /**
     * 设置字段名称
     * <p>
     * 该方法会清空字段供应商和缓存的类型描述符，下次访问时将重新查找字段
     * </p>
     * 
     * @param name 字段名称，不可为null
     */
    public synchronized void setName(@NonNull String name) {
        this.name = name;
        this.fieldSupplier = null;
        this.typeDescriptor = null;
    }

    /**
     * 获取反射Field对象（延迟初始化）
     * <p>
     * 该方法使用双重检查锁机制延迟查找字段，首次访问时才会实际查找字段对象
     * </p>
     * 
     * @return 反射Field对象，若未找到则可能返回null（取决于查找策略）
     */
    public Field getField() {
        if (fieldSupplier == null) {
            synchronized (this) {
                if (fieldSupplier == null) {
                    Field field = name == null ? null
                            : ReflectionUtils.findDeclaredField(declaringClass, name).withAll().first();
                    fieldSupplier = () -> field;
                }
            }
        }
        return fieldSupplier.get();
    }

    /**
     * 获取字段名称
     * 
     * @return 字段名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取字段所属的声明类
     * 
     * @return 声明类的Class对象
     */
    public final Class<?> getDeclaringClass() {
        return declaringClass;
    }

    /**
     * 获取字段的类型描述符（延迟初始化）
     * <p>
     * 该方法使用双重检查锁机制延迟创建类型描述符，首次访问时才会实际获取字段类型
     * </p>
     * 
     * @return 字段的类型描述符
     */
    public TypeDescriptor getTypeDescriptor() {
        if (typeDescriptor == null) {
            synchronized (this) {
                if (typeDescriptor == null) {
                    Field field = getField();
                    typeDescriptor = field == null ? TypeDescriptor.valueOf(Object.class)
                            : TypeDescriptor.forFieldType(getField());
                }
            }
        }
        return typeDescriptor;
    }

    /**
     * 获取属性的返回类型描述符（与字段类型相同）
     * 
     * @return 字段的类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 获取属性所需的类型描述符（与字段类型相同）
     * 
     * @return 字段的类型描述符
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 判断字段是否可读
     * <p>
     * 只要成功获取到字段对象即视为可读
     * </p>
     * 
     * @return 如果获取到字段对象返回true，否则返回false
     */
    @Override
    public boolean isReadable() {
        return getField() != null;
    }

    /**
     * 判断字段是否可写
     * <p>
     * 只要成功获取到字段对象即视为可写（忽略访问权限控制）
     * </p>
     * 
     * @return 如果获取到字段对象返回true，否则返回false
     */
    @Override
    public boolean isWriteable() {
        return getField() != null;
    }

    /**
     * 向字段写入值
     * <p>
     * 该方法通过反射设置字段的值，会自动处理访问权限
     * </p>
     * 
     * @param target 目标对象
     * @param value 要写入的值
     */
    @Override
    public void writeTo(Object target, Object value) {
        ReflectionUtils.set(getField(), target, value);
    }

    /**
     * 从字段读取值
     * <p>
     * 该方法通过反射获取字段的值，会自动处理访问权限
     * </p>
     * 
     * @param target 目标对象
     * @return 字段的值
     */
    @Override
    public Object readFrom(Object target) {
        return ReflectionUtils.get(getField(), target);
    }

    /**
     * 获取字段的字符串表示
     * 
     * @return 字段的字符串表示，若未找到字段则返回null
     */
    @Override
    public String toString() {
        Field field = getField();
        return field == null ? null : field.toString();
    }
}