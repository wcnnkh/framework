package run.soeasy.framework.core.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import lombok.Getter;

/**
 * 数组类型解析器，用于表示和解析Java中的泛型数组类型，
 * 继承自{@link AbstractResolvableType}并针对数组类型特性进行实现。
 * 该类封装了{@link GenericArrayType}接口，支持获取数组组件类型和原始类型信息，
 * 适用于反射操作、泛型类型解析等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>泛型数组支持：封装GenericArrayType接口，处理带泛型的数组类型</li>
 *   <li>组件类型解析：提供获取数组组件类型的能力（如List&lt;String&gt;[]的组件类型为List&lt;String&gt;）</li>
 *   <li>原始类型映射：将数组类型映射到Object[]类，作为数组的公共基类型</li>
 *   <li>类型变量处理：通过类型变量解析器处理泛型组件类型中的变量</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射操作：获取泛型数组的类型信息</li>
 *   <li>序列化处理：处理泛型数组的序列化和反序列化</li>
 *   <li>框架开发：实现支持泛型数组的容器或工具类</li>
 *   <li>类型验证：验证方法参数或返回值是否为泛型数组</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建泛型数组类型：List&lt;String&gt;[]
 * Type listStringType = new TypeToken&lt;List&lt;String&gt;&gt;(){}.getType();
 * GenericArrayType arrayType = new GenericArrayType() {
 *     public Type getGenericComponentType() {
 *         return listStringType;
 *     }
 * };
 * 
 * // 解析数组类型
 * ArrayType resolvableArrayType = new ArrayType(arrayType, TypeVariableResolver.DEFAULT);
 * System.out.println("是否为数组: " + resolvableArrayType.isArray()); // 输出: true
 * 
 * // 获取组件类型
 * ResolvableType componentType = resolvableArrayType.getComponentType();
 * System.out.println("组件类型: " + componentType); // 输出: java.util.List&lt;java.lang.String&gt;
 * </pre>
 *
 * @see AbstractResolvableType
 * @see GenericArrayType
 * @see TypeVariableResolver
 */
@Getter
public class ArrayType extends AbstractResolvableType<GenericArrayType> {

    /**
     * 创建数组类型解析器。
     * <p>
     * 该构造函数用于初始化数组类型解析器，关联泛型数组类型和类型变量解析器。
     *
     * @param genericArrayType 泛型数组类型对象，不可为null
     * @param typeVariableResolver 类型变量解析器，不可为null
     */
    public ArrayType(GenericArrayType genericArrayType, TypeVariableResolver typeVariableResolver) {
        super(genericArrayType, typeVariableResolver);
    }

    /**
     * 获取数组的组件类型。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从GenericArrayType中获取泛型组件类型</li>
     *   <li>使用当前类型变量解析器将组件类型转换为ResolvableType</li>
     * </ol>
     * 例如，对于List&lt;String&gt;[]类型，返回List&lt;String&gt;的ResolvableType表示。
     *
     * @return 数组组件类型的ResolvableType表示
     */
    @Override
    public ResolvableType getComponentType() {
        Type type = getType().getGenericComponentType();
        return ResolvableType.forType(type, this.getTypeVariableResolver());
    }

    /**
     * 判断数组类型是否包含实际类型参数（始终返回false）。
     * <p>
     * 数组类型本身不包含类型参数，其类型参数由组件类型定义，因此返回false。
     *
     * @return false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return false;
    }

    /**
     * 获取数组类型的实际类型参数（始终返回空数组）。
     * <p>
     * 数组类型本身不包含类型参数，其类型参数由组件类型定义，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 判断是否为数组类型（始终返回true）。
     * <p>
     * 该类专门处理数组类型，因此始终返回true。
     *
     * @return true
     */
    @Override
    public boolean isArray() {
        return true;
    }

    /**
     * 获取类型变量的下界（数组类型不支持，返回空数组）。
     * <p>
     * 数组类型不定义类型变量，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getLowerBounds() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 获取内部类的外部类型（数组类型不支持，返回null）。
     * <p>
     * 数组类型不是内部类，因此返回null。
     *
     * @return null
     */
    @Override
    public ResolvableType getOwnerType() {
        return null;
    }

    /**
     * 获取数组类型的原始类型（固定返回Object[].class）。
     * <p>
     * 所有数组的公共基类型为Object[]，因此返回Object[].class。
     *
     * @return Object[].class
     */
    @Override
    public Class<?> getRawType() {
        return Object[].class;
    }

    /**
     * 获取类型变量的上界（数组类型不支持，返回空数组）。
     * <p>
     * 数组类型不定义类型变量，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getUpperBounds() {
        return EMPTY_TYPES_ARRAY;
    }
}