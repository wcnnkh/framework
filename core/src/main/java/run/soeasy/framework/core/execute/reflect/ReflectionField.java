package run.soeasy.framework.core.execute.reflect;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * 反射字段封装类，实现{@link Property}接口和{@link Serializable}接口，
 * 统一封装Java反射Field的访问逻辑，提供标准化的字段读写能力。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>严格的空值校验：所有核心入参通过Lombok @NonNull强制非空，反序列化校验兜底</li>
 *   <li>并发安全设计：双重检查锁+同步块内读取最新字段名，避免多线程下字段查找错误</li>
 *   <li>序列化安全：保留Field唯一标识，反序列化精准恢复自定义Field实例，降级逻辑兜底</li>
 *   <li>强制读写能力：突破访问权限和final修饰符限制，适配灵活的字段操作需求</li>
 *   <li>延迟初始化：字段查找和类型描述符均延迟加载，降低初始化开销</li>
 * </ul>
 *
 * <p><b>适用场景：</b>
 * <ul>
 *   <li>对象映射：不同对象间字段值的自动映射（如DTO→DO）</li>
 *   <li>序列化/反序列化：自定义字段级的序列化规则</li>
 *   <li>配置绑定：配置项与Bean字段的动态绑定</li>
 *   <li>测试/诊断：运行时访问私有/final字段</li>
 *   <li>AOP/增强：动态修改对象字段值</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Property
 * @see java.lang.reflect.Field
 */
public class ReflectionField implements Property, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字段所属的声明类（不可变核心标识）
     */
    private final Class<?> declaringClass;

    /**
     * 字段名称（volatile保证多线程可见性）
     */
    private volatile String name;

    /**
     * 延迟加载的Field供应商（transient避免序列化，性能优化）
     */
    private transient volatile Supplier<Field> fieldSupplier;

    /**
     * 字段类型描述符（延迟初始化，缓存复用）
     */
    private transient volatile TypeDescriptor typeDescriptor;

    /**
     * Field唯一签名（类名#字段名#类型），用于序列化精准恢复Field实例
     * 解决transient的fieldSupplier序列化丢失问题，保障序列化安全性
     */
    private String fieldSignature;

    // ====================== 构造器 ======================

    /**
     * 基于声明类+字段名构建反射字段
     *
     * @param declaringClass 字段所属类，核心参数，不可为null
     * @param name           字段名称，核心参数，不可为null
     */
    public ReflectionField(@NonNull Class<?> declaringClass, @NonNull String name) {
        this.declaringClass = declaringClass;
        this.name = name;
    }

    /**
     * 基于已有Field实例构建反射字段
     *
     * @param field 已有的Field实例，不可为null
     */
    public ReflectionField(@NonNull Field field) {
        this(field.getDeclaringClass(), field.getName());
        this.fieldSupplier = () -> field;
        this.fieldSignature = buildFieldSignature(field);
    }

    // ====================== 核心操作方法 ======================

    /**
     * 设置自定义Field实例
     *
     * @param field 自定义Field实例，不可为null
     */
    public synchronized void setField(@NonNull Field field) {
        this.fieldSupplier = () -> field;
        this.name = field.getName();
        this.fieldSignature = buildFieldSignature(field);
        this.typeDescriptor = null; // 重置类型描述符，保证和新Field一致
    }

    /**
     * 修改字段名称
     * 修改后会重置Field供应商和类型描述符，下次访问重新查找
     *
     * @param name 新的字段名称，不可为null
     */
    public synchronized void setName(@NonNull String name) {
        this.name = name;
        this.fieldSupplier = null;
        this.fieldSignature = null;
        this.typeDescriptor = null;
    }

    /**
     * 获取Field实例（延迟加载+并发安全）
     * 双重检查锁保证懒加载，同步块内读取最新name，避免多线程查找错误
     *
     * @return Field实例，未找到则返回null
     */
    public Field getField() {
        if (fieldSupplier == null) {
            synchronized (this) {
                if (fieldSupplier == null) {
                    String currentName = this.name;
                    Field field = (currentName == null || currentName.isEmpty())
                            ? null
                            : ReflectionUtils.findDeclaredField(declaringClass, currentName).withAll().first();
                    fieldSupplier = () -> field;
                }
            }
        }
        return fieldSupplier.get();
    }

    // ====================== Property接口实现 ======================

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return getTypeDescriptor();
    }

    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        return getTypeDescriptor();
    }

    @Override
    public boolean isReadable() {
        return getField() != null;
    }

    @Override
    public boolean isWriteable() {
        // 仅判断字段存在，忽略final/访问权限，由ReflectionUtils突破限制
        return getField() != null;
    }

    @Override
    public void writeTo(Object target, Object value) {
        Field field = getField();
        if (field == null) {
            throw new IllegalStateException(
                    String.format("字段访问失败：类[%s]中未找到字段[%s]", declaringClass.getName(), name)
            );
        }
        ReflectionUtils.set(field, target, value);
    }

    @Override
    public Object readFrom(Object target) {
        Field field = getField();
        if (field == null) {
            throw new IllegalStateException(
                    String.format("字段访问失败：类[%s]中未找到字段[%s]", declaringClass.getName(), name)
            );
        }
        return ReflectionUtils.get(field, target);
    }

    // ====================== 辅助方法 ======================

    /**
     * 获取字段所属声明类
     */
    public final Class<?> getDeclaringClass() {
        return declaringClass;
    }

    /**
     * 获取字段类型描述符（延迟初始化，缓存复用）
     * 双重检查锁延迟初始化，避免重复构建TypeDescriptor
     */
    public TypeDescriptor getTypeDescriptor() {
        if (typeDescriptor == null) {
            synchronized (this) {
                if (typeDescriptor == null) {
                    Field field = getField();
                    typeDescriptor = field == null
                            ? TypeDescriptor.valueOf(Object.class)
                            : TypeDescriptor.forField(field);
                }
            }
        }
        return typeDescriptor;
    }

    /**
     * 构建Field唯一签名（类名#字段名#类型）
     * 解决不同类加载器/父子类同名字段的歧义问题
     */
    private String buildFieldSignature(Field field) {
        return String.format("%s#%s#%s",
                field.getDeclaringClass().getName(),
                field.getName(),
                field.getType().getName());
    }

    /**
     * 反序列化校验与恢复
     * 1. 校验核心字段非空，避免反序列化绕过构造器校验
     * 2. 尝试恢复自定义Field实例，失败则降级为按名称查找
     */
    private Object readResolve() throws ObjectStreamException {
        // 核心校验：声明类和字段名不可为空
        if (declaringClass == null) {
            throw new InvalidObjectException("字段访问异常：declaringClass不可为null");
        }
        if (name == null || name.isEmpty()) {
            throw new InvalidObjectException("字段访问异常：字段名称不可为null/空");
        }

        // 尝试恢复自定义Field实例
        if (fieldSignature != null && !fieldSignature.isEmpty()) {
            try {
                String[] parts = fieldSignature.split("#");
                Class<?> fieldClass = Class.forName(parts[0]);
                Field field = fieldClass.getDeclaredField(parts[1]);
                this.setField(field);
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                // 恢复失败，降级为按名称重新查找
                this.fieldSupplier = null;
            }
        }

        return this;
    }

    @Override
    public String toString() {
        Field field = getField();
        if (field == null) {
            return String.format("ReflectionField[declaringClass=%s, name=%s, state=not_found]",
                    declaringClass.getName(), name);
        }
        return String.format("ReflectionField[declaringClass=%s, name=%s, type=%s, modifiers=%s]",
                declaringClass.getName(), name, field.getType().getName(), field.getModifiers());
    }
}