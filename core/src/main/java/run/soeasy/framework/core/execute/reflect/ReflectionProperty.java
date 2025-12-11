package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.Property;

/**
 * 反射属性实现类，继承自{@link ReflectionField}并实现{@link Property}接口，
 * 提供JavaBean属性的动态访问能力，支持通过字段（Field）或方法（Method）两种方式操作属性。
 * <p>
 * 该类支持灵活配置属性的读写方式：可通过字段直接访问，或通过getter/setter方法访问，
 * 并支持混合模式（读用方法、写用字段等）。实现了序列化接口，便于在远程调用或持久化场景中使用。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>属性抽象：统一字段与方法的属性访问方式，实现{@link Property}接口</li>
 *   <li>多模式访问：支持通过字段（FIELD）、方法（METHOD）或默认（混合）模式操作属性</li>
 *   <li>动态配置：可显式设置读写方法，覆盖默认的属性访问逻辑</li>
 *   <li>类型适配：根据访问模式自动适配属性的类型描述符</li>
 *   <li>序列化支持：实现Serializable接口，支持属性访问器的序列化与反序列化</li>
 * </ul>
 *
 * <p><b>访问模式说明：</b>
 * <ul>
 *   <li>{@link InvokeType#FIELD}：直接通过字段访问属性</li>
 *   <li>{@link InvokeType#METHOD}：通过getter/setter方法访问属性</li>
 *   <li>{@link InvokeType#DEFAULT}：优先通过方法访问，无方法时自动 fallback 到字段</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Bean属性操作：统一访问JavaBean的字段和方法属性</li>
 *   <li>属性映射：在不同对象的属性间进行值映射</li>
 *   <li>动态Bean：动态创建和操作Bean的属性</li>
 *   <li>ORM框架：实现对象与数据库字段的映射</li>
 *   <li>配置管理：动态读取和设置对象的配置属性</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Property
 * @see ReflectionField
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ReflectionProperty extends ReflectionField implements Property, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 属性访问模式枚举，定义属性的读写方式
     */
    public enum InvokeType {
        /** 默认模式：优先使用方法，无方法时使用字段 */
        DEFAULT,
        /** 字段模式：直接通过字段访问属性 */
        FIELD,
        /** 方法模式：通过getter/setter方法访问属性 */
        METHOD
    }

    /** 读取属性的方法包装器，可为null */
    private ReflectionMethod readMethod;
    
    /** 写入属性的方法包装器，可为null */
    private ReflectionMethod writeMethod;
    
    /** 读取属性时使用的访问模式，默认{@link InvokeType#DEFAULT} */
    @NonNull
    private InvokeType readType = InvokeType.DEFAULT;
    
    /** 写入属性时使用的访问模式，默认{@link InvokeType#DEFAULT} */
    @NonNull
    private InvokeType writeType = InvokeType.DEFAULT;

    /**
     * 构造函数，通过声明类和属性名称初始化反射属性
     * 
     * @param declaringClass 属性所属的声明类，不可为null
     * @param propertyName 属性名称，不可为null
     */
    public ReflectionProperty(@NonNull Class<?> declaringClass, @NonNull String propertyName) {
        super(declaringClass, propertyName);
    }

    /**
     * 设置读取属性的方法
     * 
     * @param method 反射Method对象，可为null（表示不使用方法读取）
     */
    public void setReadMethod(Method method) {
        this.readMethod = method == null ? null : new ReflectionMethod(method);
    }

    /**
     * 设置写入属性的方法
     * 
     * @param method 反射Method对象，可为null（表示不使用方法写入）
     */
    public void setWriteMethod(Method method) {
        this.writeMethod = method == null ? null : new ReflectionMethod(method);
    }

    /**
     * 获取属性所需的类型描述符（用于写入操作）
     * <p>
     * 根据写入模式决定使用字段类型还是写入方法的参数类型：
     * <ul>
     *   <li>FIELD模式：使用字段类型</li>
     *   <li>METHOD模式：使用写入方法的参数类型</li>
     *   <li>DEFAULT模式：优先使用写入方法，无方法时使用字段类型</li>
     * </ul>
     * 
     * @return 属性所需的类型描述符
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        if (writeType == InvokeType.FIELD || writeMethod == null) {
            return super.getRequiredTypeDescriptor();
        } else {
            return new TypeDescriptor(writeMethod.getRequiredTypeDescriptor().getResolvableType(), null, writeMethod,
                    getField());
        }
    }

    /**
     * 获取属性的返回类型描述符（用于读取操作）
     * <p>
     * 根据读取模式决定使用字段类型还是读取方法的返回类型：
     * <ul>
     *   <li>FIELD模式：使用字段类型</li>
     *   <li>METHOD模式：使用读取方法的返回类型</li>
     *   <li>DEFAULT模式：优先使用读取方法，无方法时使用字段类型</li>
     * </ul>
     * 
     * @return 属性的返回类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        if (readType == InvokeType.FIELD || readMethod == null) {
            return super.getReturnTypeDescriptor();
        } else {
            return new TypeDescriptor(readMethod.getReturnTypeDescriptor().getResolvableType(), null, readMethod,
                    getField());
        }
    }

    /**
     * 判断属性是否可读
     * <p>
     * 根据读取模式检查对应访问方式是否可用：
     * <ul>
     *   <li>FIELD模式：检查字段是否存在</li>
     *   <li>METHOD模式：检查读取方法是否存在且可读</li>
     *   <li>DEFAULT模式：只要字段或方法任意一种可用即返回true</li>
     * </ul>
     * 
     * @return 属性是否可读
     */
    @Override
    public boolean isReadable() {
        return isReadable(this.readType);
    }

    /**
     * 判断指定模式下属性是否可读（内部辅助方法）
     */
    private boolean isReadable(InvokeType invokeType) {
        if (invokeType == InvokeType.FIELD) {
            return super.isReadable();
        } else if (invokeType == InvokeType.METHOD) {
            return readMethod != null && readMethod.isReadable();
        }
        return isReadable(InvokeType.FIELD) || isReadable(InvokeType.METHOD);
    }

    /**
     * 判断属性是否可写
     * <p>
     * 根据写入模式检查对应访问方式是否可用：
     * <ul>
     *   <li>FIELD模式：检查字段是否存在</li>
     *   <li>METHOD模式：检查写入方法是否存在且可写</li>
     *   <li>DEFAULT模式：只要字段或方法任意一种可用即返回true</li>
     * </ul>
     * 
     * @return 属性是否可写
     */
    @Override
    public boolean isWriteable() {
        return isWriteable(this.writeType);
    }

    /**
     * 判断指定模式下属性是否可写（内部辅助方法）
     */
    private boolean isWriteable(InvokeType invokeType) {
        if (invokeType == InvokeType.FIELD) {
            return super.isWriteable();
        } else if (invokeType == InvokeType.METHOD) {
            return writeMethod != null && writeMethod.isWriteable();
        }
        return isWriteable(InvokeType.FIELD) || isWriteable(InvokeType.METHOD);
    }

    /**
     * 从属性读取值
     * <p>
     * 根据读取模式选择字段或方法读取值：
     * <ul>
     *   <li>FIELD模式：通过字段读取</li>
     *   <li>METHOD模式：通过读取方法读取</li>
     *   <li>DEFAULT模式：优先通过方法读取，无方法时通过字段读取</li>
     * </ul>
     * 
     * @param target 目标对象
     * @return 属性值
     */
    @Override
    public Object readFrom(Object target) {
        if (readType == InvokeType.FIELD || readMethod == null) {
            return super.readFrom(target);
        } else {
            return readMethod.readFrom(target);
        }
    }

    /**
     * 向属性写入值
     * <p>
     * 根据写入模式选择字段或方法写入值：
     * <ul>
     *   <li>FIELD模式：通过字段写入</li>
     *   <li>METHOD模式：通过写入方法写入</li>
     *   <li>DEFAULT模式：优先通过方法写入，无方法时通过字段写入</li>
     * </ul>
     * 
     * @param target 目标对象
     * @param value 要写入的值
     */
    @Override
    public void writeTo(Object target, Object value) {
        if (writeType == InvokeType.FIELD || writeMethod == null) {
            super.writeTo(target, value);
        } else {
            writeMethod.writeTo(target, value);
        }
    }
}