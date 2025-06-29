package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 可解析类型的抽象实现
 * 提供类型解析的基础功能，实现ResolvableType接口
 * 
 * @author soeasy.run
 * @param <T> 类型参数，继承自Type
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "type")
public abstract class AbstractResolvableType<T extends Type> implements ResolvableType {
    /** 被解析的类型对象 */
    @NonNull
    private final T type;
    
    /** 类型变量解析器 */
    private TypeVariableResolver typeVariableResolver;

    /**
     * 解析类型变量
     * 先尝试使用接口默认解析逻辑，失败时使用类型变量解析器
     * 
     * @param typeVariable 待解析的类型变量
     * @return 解析后的可解析类型，解析失败返回null
     */
    @Override
    public ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
        ResolvableType resolvableType = ResolvableType.super.resolveTypeVariable(typeVariable);
        if (resolvableType == null && typeVariableResolver != null) {
            resolvableType = typeVariableResolver.resolveTypeVariable(typeVariable);
        }
        return resolvableType;
    }

    /**
     * 返回类型的字符串表示
     * 直接返回类型名称
     */
    @Override
    public final String toString() {
        return getTypeName();
    }
}