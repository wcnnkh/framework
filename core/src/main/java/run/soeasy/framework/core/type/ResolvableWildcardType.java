package run.soeasy.framework.core.type;

import java.lang.reflect.WildcardType;

import lombok.NonNull;

/**
 * 通配符类型解析器，用于表示和解析Java中的泛型通配符类型（Wildcard Type），
 * 继承自{@link AbstractResolvableType}并针对通配符类型特性进行实现。
 * 该类封装了{@link WildcardType}接口，支持获取通配符的上界和下界约束，
 * 适用于反射操作、泛型类型解析、通配符类型处理等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>通配符封装：封装Java反射中的WildcardType接口（如? extends Number）</li>
 *   <li>边界解析：支持获取通配符的上界（extends）和下界（super）约束</li>
 *   <li>类型安全：提供通配符类型的标准化解析接口</li>
 *   <li>空类型参数：通配符本身不包含实际类型参数，始终返回空数组</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型类型解析：解析包含通配符的泛型类型（如List&lt;? extends Number&gt;）</li>
 *   <li>反射操作：获取方法参数或返回值中的通配符类型信息</li>
 *   <li>框架开发：在泛型组件中处理通配符类型约束</li>
 *   <li>类型验证：验证通配符类型的上下界约束</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建通配符类型：? extends Number
 * WildcardType wildcardType = new WildcardType() {
 *     public Type[] getUpperBounds() {
 *         return new Type[]{Number.class};
 *     }
 *     public Type[] getLowerBounds() {
 *         return new Type[0];
 *     }
 * };
 * 
 * // 解析通配符类型
 * ResolvableWildcardType resolvableWildcard = new ResolvableWildcardType(
 *     wildcardType, 
 *     TypeVariableResolver.DEFAULT
 * );
 * 
 * // 获取上界
 * ResolvableType[] upperBounds = resolvableWildcard.getUpperBounds();
 * System.out.println("通配符上界: " + upperBounds[0].getTypeName()); // 输出: java.lang.Number
 * 
 * // 获取下界（无下界时返回空数组）
 * ResolvableType[] lowerBounds = resolvableWildcard.getLowerBounds();
 * System.out.println("通配符下界数量: " + lowerBounds.length); // 输出: 0
 * </pre>
 *
 * @see AbstractResolvableType
 * @see WildcardType
 * @see TypeVariableResolver
 */
public class ResolvableWildcardType extends AbstractResolvableType<WildcardType> {

    /**
     * 创建通配符类型解析器。
     * <p>
     * 该构造函数用于初始化通配符类型解析器，关联通配符类型和类型变量解析器。
     *
     * @param type 通配符类型对象（如? extends Number），不可为null
     * @param typeVariableResolver 类型变量解析器，可为null（使用默认解析器）
     * @throws NullPointerException 当type为null时抛出
     */
    public ResolvableWildcardType(@NonNull WildcardType type, TypeVariableResolver typeVariableResolver) {
        super(type, typeVariableResolver);
    }

    /**
     * 获取通配符的下界约束（super关键字指定的边界）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从WildcardType中获取声明的下界类型数组</li>
     *   <li>使用类型变量解析器将下界类型转换为ResolvableType数组</li>
     * </ol>
     * 例如，对于? super Integer，返回包含Integer的ResolvableType数组；
     * 对于无下界的通配符（如?），返回空数组。
     *
     * @return 通配符下界的ResolvableType数组，若无下界则返回空数组
     */
    @Override
    public ResolvableType[] getLowerBounds() {
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getLowerBounds());
    }

    /**
     * 获取通配符的上界约束（extends关键字指定的边界）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从WildcardType中获取声明的上界类型数组</li>
     *   <li>使用类型变量解析器将上界类型转换为ResolvableType数组</li>
     * </ol>
     * 例如，对于? extends Number，返回包含Number的ResolvableType数组；
     * 对于无显式上界的通配符（如?），返回包含Object的ResolvableType数组。
     *
     * @return 通配符上界的ResolvableType数组，若无显式上界则包含Object
     */
    @Override
    public ResolvableType[] getUpperBounds() {
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getUpperBounds());
    }

    /**
     * 判断通配符类型是否包含实际类型参数（始终返回false）。
     * <p>
     * 通配符类型本身不包含实际类型参数（如?不是具体类型），因此返回false。
     *
     * @return false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return false;
    }

    /**
     * 获取通配符类型的实际类型参数（始终返回空数组）。
     * <p>
     * 通配符类型本身不包含实际类型参数，因此返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 判断是否为数组类型（始终返回false）。
     * <p>
     * 通配符类型不表示数组，因此返回false。
     *
     * @return false
     */
    @Override
    public boolean isArray() {
        return false;
    }

    /**
     * 获取数组组件类型（始终返回null）。
     * <p>
     * 通配符类型不是数组，因此返回null。
     *
     * @return null
     */
    @Override
    public ResolvableType getComponentType() {
        return null;
    }

    /**
     * 获取内部类的外部类型（始终返回null）。
     * <p>
     * 通配符类型不是内部类，因此返回null。
     *
     * @return null
     */
    @Override
    public ResolvableType getOwnerType() {
        return null;
    }

    /**
     * 获取通配符类型的原始类型（固定返回Object.class）。
     * <p>
     * 通配符类型的原始类型映射为Object.class，作为所有类型的公共基类型。
     *
     * @return Object.class
     */
    @Override
    public Class<?> getRawType() {
        return Object.class;
    }
}