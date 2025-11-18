package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 可解析类型的抽象实现
 * <p>
 * 提供ResolvableType接口的基础实现，封装了核心属性（被解析类型+类型变量解析器），
 * 实现了类型变量解析、toString等通用逻辑，并规范了equals和hashCode的实现规范：
 * <ul>
 * <li>equals：基于compareTo结果判断，与排序逻辑强绑定（compareTo返回0则equals为true）</li>
 * <li>hashCode：基于compareTo依赖的核心属性计算，确保equals为true时hashCode必相等</li>
 * </ul>
 * 子类需实现ResolvableType接口的抽象方法，完成具体类型（如泛型、数组、通配符）的解析逻辑。
 *
 * @author soeasy.run
 * @param <T> 类型参数，继承自Type，代表被解析的原始类型
 * @see ResolvableType
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class AbstractResolvableType<T extends Type> implements ResolvableType {
    /**
     * 被解析的原始类型对象（不可为null）
     * <p>
     * 可为Class、ParameterizedType、WildcardType等Type的实现类，是类型解析的核心基准
     */
    @NonNull
    private final T type;
    
    /**
     * 类型变量解析器（可为null）
     * <p>
     * 用于解析泛型类型中的类型变量（如&lt;T&gt;），补充默认解析逻辑的不足，支持自定义变量绑定
     */
    private TypeVariableResolver typeVariableResolver;

    /**
     * 解析类型变量，获取其绑定的实际类型
     * <p>
     * 解析优先级：先调用ResolvableType接口的默认解析逻辑，解析失败时使用当前类的类型变量解析器（若存在）
     *
     * @param typeVariable 待解析的类型变量（如泛型中的&lt;T&gt;、&lt;E&gt;）
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
     * 返回类型的字符串表示，与getTypeName保持一致
     * <p>
     * 直接复用getTypeName的结果，确保类型的字符串表示包含泛型、数组等完整信息
     *
     * @return 类型的完整名称字符串（如"java.util.List&lt;java.lang.String&gt;"、"java.lang.String[]"）
     */
    @Override
    public final String toString() {
        return getTypeName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj instanceof Type) {
            ResolvableType resolvableType = ResolvableType.forType((Type) obj);
            return compareTo(resolvableType) == 0;
        }
        return false;
    }
    
    /**
     * 计算对象的哈希值
     * <p>
     * 设计原则：与equals和compareTo强绑定，确保equals为true的对象哈希值必相等
     * 计算依据：基于compareTo依赖的全维度核心属性，包括：
     * <ul>
     * <li>原始类型（getRawType()）</li>
     * <li>数组标识（isArray()）</li>
     * <li>数组组件类型（getComponentType()）</li>
     * <li>泛型参数数组（getActualTypeArguments()）</li>
     * <li>通配符上界数组（getUpperBounds()）</li>
     * <li>通配符下界数组（getLowerBounds()）</li>
     * </ul>
     * 数组类型通过Arrays.hashCode计算哈希值，确保内容相等的数组哈希值一致
     *
     * @return 基于核心属性计算的哈希值
     */
    @Override
    public int hashCode() {
        // 收集compareTo依赖的核心属性，确保与equals逻辑一致
        Class<?> rawType = getRawType();
        boolean isArray = isArray();
        ResolvableType componentType = isArray() ? getComponentType() : ResolvableType.NONE;
        ResolvableType[] actualGenerics = hasActualTypeArguments() ? getActualTypeArguments() : ResolvableType.EMPTY_TYPES_ARRAY;
        ResolvableType[] upperBounds = getUpperBounds();
        ResolvableType[] lowerBounds = getLowerBounds();

        // 组合计算哈希值：数组使用Arrays.hashCode保证内容相等则哈希相等
        return Objects.hash(
            rawType,
            isArray,
            componentType,
            Arrays.hashCode(actualGenerics),
            Arrays.hashCode(upperBounds),
            Arrays.hashCode(lowerBounds)
        );
    }
}