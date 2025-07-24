package run.soeasy.framework.core.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;

/**
 * 参数化类型解析器，用于表示和解析Java中的参数化类型（Parameterized Type），
 * 继承自{@link AbstractResolvableType}并针对参数化类型特性进行实现。
 * 该类封装了{@link ParameterizedType}接口，支持获取泛型类型的实际类型参数、原始类型等信息，
 * 适用于反射操作、泛型类型解析、框架开发等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>参数化类型封装：封装Java反射中的ParameterizedType接口（如List&lt;String&gt;）</li>
 *   <li>泛型参数解析：支持获取参数化类型的实际类型参数（如List&lt;String&gt;中的String）</li>
 *   <li>原始类型提取：获取参数化类型的原始类型（如List.class）</li>
 *   <li>所有者类型处理：支持获取内部类的外部类型（如Outer&lt;T&gt;.Inner）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型类型解析：在运行时解析参数化类型的实际类型参数</li>
 *   <li>反射操作：获取方法参数、字段类型中的泛型信息</li>
 *   <li>框架开发：在ORM、依赖注入框架中处理泛型类型</li>
 *   <li>类型安全操作：确保泛型类型在运行时的类型安全转换</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建参数化类型：List&lt;String&gt;
 * Type listStringType = new TypeToken&lt;List&lt;String&gt;&gt;() {}.getType();
 * 
 * // 解析参数化类型
 * ResolvableParameterizedType resolvableType = new ResolvableParameterizedType(
 *     (ParameterizedType) listStringType, 
 *     TypeVariableResolver.DEFAULT
 * );
 * 
 * // 获取原始类型
 * Class&lt;?&gt; rawType = resolvableType.getRawType();
 * System.out.println("原始类型: " + rawType.getName()); // 输出: java.util.List
 * 
 * // 获取实际类型参数
 * ResolvableType[] typeArguments = resolvableType.getActualTypeArguments();
 * System.out.println("类型参数: " + typeArguments[0].getTypeName()); // 输出: java.lang.String
 * </pre>
 *
 * @see AbstractResolvableType
 * @see ParameterizedType
 * @see TypeVariableResolver
 */
@Getter
public class ResolvableParameterizedType extends AbstractResolvableType<ParameterizedType> {

    /**
     * 创建参数化类型解析器。
     * <p>
     * 该构造函数用于初始化参数化类型解析器，关联参数化类型和类型变量解析器。
     *
     * @param type 参数化类型对象（如List&lt;String&gt;的Type表示），不可为null
     * @param typeVariableResolver 类型变量解析器，可为null（使用默认解析器）
     * @throws NullPointerException 当type为null时抛出
     */
    public ResolvableParameterizedType(@NonNull ParameterizedType type, TypeVariableResolver typeVariableResolver) {
        super(type, typeVariableResolver);
    }

    /**
     * 判断参数化类型是否包含实际类型参数。
     * <p>
     * 实现逻辑：检查ParameterizedType的实际类型参数数组长度是否大于0。
     * 例如，List&lt;String&gt;包含实际类型参数，返回true；List不包含，返回false。
     *
     * @return true如果包含实际类型参数，否则false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return getType().getActualTypeArguments().length != 0;
    }

    /**
     * 获取参数化类型的实际类型参数。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从ParameterizedType中获取原始类型参数数组</li>
     *   <li>使用类型变量解析器将每个类型参数转换为ResolvableType</li>
     * </ol>
     * 例如，对于List&lt;String&gt;，返回包含String的ResolvableType数组。
     *
     * @return 实际类型参数的ResolvableType数组，可能为空数组
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getActualTypeArguments());
    }

    /**
     * 判断是否为数组类型（始终返回false）。
     * <p>
     * 参数化类型不表示数组，因此返回false。
     *
     * @return false
     */
    @Override
    public boolean isArray() {
        return false;
    }

    /**
     * 获取数组组件类型（始终返回NONE）。
     * <p>
     * 参数化类型不是数组，因此返回NONE类型。
     *
     * @return NONE类型
     */
    @Override
    public ResolvableType getComponentType() {
        return NONE;
    }

    /**
     * 获取类型变量的下界（参数化类型不支持，返回空数组）。
     * <p>
     * 参数化类型不定义类型变量，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getLowerBounds() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 获取内部类的外部类型（参数化类型的所有者类型）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从ParameterizedType中获取所有者类型</li>
     *   <li>使用类型变量解析器将所有者类型转换为ResolvableType</li>
     * </ol>
     * 例如，对于Outer&lt;T&gt;.Inner类型，返回Outer的ResolvableType表示。
     *
     * @return 所有者类型的ResolvableType，若无所有者类型返回null
     */
    @Override
    public ResolvableType getOwnerType() {
        Type type = getType().getOwnerType();
        return type == null ? null : ResolvableType.forType(type, this.getTypeVariableResolver());
    }

    /**
     * 获取参数化类型的原始类型。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从ParameterizedType中获取原始类型</li>
     *   <li>若原始类型为Class，则直接返回；否则返回Object.class</li>
     * </ol>
     * 例如，List&lt;String&gt;的原始类型为List.class。
     *
     * @return 原始类型的Class对象，若无法获取则返回Object.class
     */
    @Override
    public Class<?> getRawType() {
        Type rawType = getType().getRawType();
        if (rawType == null) {
            return null;
        }
        return rawType instanceof Class ? ((Class<?>) rawType) : Object.class;
    }

    /**
     * 获取类型变量的上界（参数化类型不支持，返回空数组）。
     * <p>
     * 参数化类型不定义类型变量，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getUpperBounds() {
        return EMPTY_TYPES_ARRAY;
    }
}