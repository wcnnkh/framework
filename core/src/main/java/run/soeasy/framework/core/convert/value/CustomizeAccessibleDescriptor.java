package run.soeasy.framework.core.convert.value;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 自定义可访问描述符，实现{@link AccessibleDescriptor}接口，用于封装类型描述和访问权限信息。
 * <p>
 * 该类提供了可配置的类型描述符和访问控制标志，支持设置返回类型、目标类型、
 * 非空约束、可读/可写权限，适用于需要自定义类型描述和访问控制的场景。
 * </p>
 *
 * <p><b>实现细节：</b>
 * <ul>
 *   <li>实现{@link Serializable}接口，支持序列化</li>
 *   <li>通过字段配置方式定义类型描述和访问权限</li>
 *   <li>提供构造函数快速初始化类型描述符</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>字段{@code requried}存在拼写错误，应为{@code required}</li>
 *   <li>未对{@link TypeDescriptor}参数进行非空校验（依赖Lombok的{@code @NonNull}注解）</li>
 *   <li>序列化版本号{@code serialVersionUID}为1，未明确版本迭代策略</li>
 *   <li>未重写{@link Object#equals(Object)}和{@link Object#hashCode()}方法，可能导致逻辑错误</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AccessibleDescriptor
 * @see TypeDescriptor
 */
@Data
public class CustomizeAccessibleDescriptor implements AccessibleDescriptor, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 返回类型描述符，不可为null */
    @NonNull
    private TypeDescriptor returnTypeDescriptor;
    
    /** 目标类型描述符，不可为null */
    @NonNull
    private TypeDescriptor requiredTypeDescriptor;
    
    /** 是否为必需（存在拼写错误，应为required） */
    private boolean requried = false;
    
    /** 是否可读，默认true */
    private boolean readable = true;
    
    /** 是否可写，默认true */
    private boolean writeable = true;

    /**
     * 构造函数，使用指定类型描述符初始化可访问描述符
     * <p>
     * 自动将返回类型和目标类型设置为同一描述符，适用于源类型和目标类型一致的场景。
     * 
     * @param typeDescriptor 类型描述符，不可为null
     */
    public CustomizeAccessibleDescriptor(@NonNull TypeDescriptor typeDescriptor) {
        this.requiredTypeDescriptor = typeDescriptor;
        this.returnTypeDescriptor = typeDescriptor;
    }

    // --- 以下为接口方法实现（隐式通过Lombok的@Data生成）---
    
    /**
     * 获取返回类型描述符（实现{@link AccessibleDescriptor}接口）
     * @return 返回类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return returnTypeDescriptor;
    }

    /**
     * 获取目标类型描述符（实现{@link AccessibleDescriptor}接口）
     * @return 目标类型描述符
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        return requiredTypeDescriptor;
    }

    /**
     * 判断是否为必需（实现{@link TargetDescriptor}接口）
     * <p>
     * 注意：字段名存在拼写错误，实际返回{@code requried}字段值
     * @return 是否为必需
     */
    @Override
    public boolean isRequired() {
        return requried;
    }

    /**
     * 判断是否可读（实现{@link AccessibleDescriptor}接口）
     * @return 是否可读
     */
    @Override
    public boolean isReadable() {
        return readable;
    }

    /**
     * 判断是否可写（实现{@link AccessibleDescriptor}接口）
     * @return 是否可写
     */
    @Override
    public boolean isWriteable() {
        return writeable;
    }
}