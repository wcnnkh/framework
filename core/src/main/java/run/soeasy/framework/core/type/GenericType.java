package run.soeasy.framework.core.type;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 表示参数化类型（泛型类型）的可解析类型实现。
 * 该类扩展自{@link RawType}，并添加了对泛型类型参数和所有者类型的支持，
 * 用于在运行时解析和处理泛型类型信息。
 *
 * <p>核心特性：
 * <ul>
 *   <li>泛型参数支持：通过{@link #actualTypeArguments}存储和访问泛型参数</li>
 *   <li>所有者类型：通过{@link #ownerType}支持内部类的泛型类型信息</li>
 *   <li>类型解析：提供方法将原始类型信息转换为可解析的类型表示</li>
 *   <li>继承扩展：继承自{@link RawType}，复用原始类型的基本功能</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型反射：在运行时获取和处理泛型类型信息</li>
 *   <li>框架开发：实现依赖注入、序列化等需要处理泛型的框架</li>
 *   <li>工具类库：开发处理泛型类型的工具类</li>
 *   <li>代码生成：根据泛型类型信息生成代码</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建表示List&lt;String&gt;的GenericType
 * GenericType listOfStringType = new GenericType(List.class);
 * listOfStringType.setActualTypeArguments(String.class);
 * 
 * // 获取泛型参数
 * ResolvableType[] typeArguments = listOfStringType.getActualTypeArguments();
 * if (typeArguments.length &gt; 0) {
 *     System.out.println("第一个泛型参数类型: " + typeArguments[0].getRawType());
 * }
 * 
 * // 判断是否有泛型参数
 * boolean hasArguments = listOfStringType.hasActualTypeArguments();
 * </pre>
 *
 * @see ResolvableType
 * @see RawType
 */
@Getter
@Setter
public class GenericType extends RawType {
    /** 空类型数组常量 */
    private static final Type[] EMPTY_TYPES_ARRAY = new Type[0];
    
    /** 实际泛型类型参数，不可为null */
    @NonNull
    private Type[] actualTypeArguments = EMPTY_TYPES_ARRAY;
    
    /** 所有者类型，用于表示内部类的外部类泛型信息 */
    private Type ownerType;

    /**
     * 创建一个表示指定原始类型的泛型类型实例。
     *
     * @param rawType 原始类型，不可为null
     */
    public GenericType(@NonNull Class<?> rawType) {
        super(rawType);
    }

    /**
     * 获取泛型类型的实际类型参数。
     *
     * @return 实际类型参数的可解析类型数组
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), actualTypeArguments);
    }

    /**
     * 获取所有者类型的可解析类型表示。
     *
     * @return 所有者类型的可解析类型，如果没有则返回null
     */
    @Override
    public ResolvableType getOwnerType() {
        return ownerType == null ? null : ResolvableType.forType(ownerType, this.getTypeVariableResolver());
    }

    /**
     * 判断该泛型类型是否包含实际类型参数。
     *
     * @return 如果包含实际类型参数返回true，否则返回false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return actualTypeArguments == null ? super.hasActualTypeArguments() : actualTypeArguments.length != 0;
    }
}