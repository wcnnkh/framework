package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;

import lombok.Getter;
import lombok.NonNull;

/**
 * 可解析的类型变量解析器，用于表示和处理Java中的泛型类型变量（Type Variable），
 * 继承自{@link AbstractResolvableType}并实现{@link ResolvableTypeWrapper}接口。
 * 该类封装了{@link TypeVariable}接口，支持获取类型变量的边界信息和解析后的实际类型，
 * 适用于反射操作、泛型类型解析、类型变量替换等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型变量封装：封装Java反射中的TypeVariable接口，处理泛型类型变量（如T, E）</li>
 *   <li>边界解析：支持获取类型变量的上界和下界约束（如T extends Number &amp; Comparable）</li>
 *   <li>动态解析：通过{@link TypeVariableResolver}动态解析类型变量为实际类型</li>
 *   <li>类型包装：实现ResolvableTypeWrapper接口，提供统一的类型访问接口</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射操作：获取泛型类或方法中类型变量的定义和约束</li>
 *   <li>泛型类型解析：在运行时解析泛型类型变量的实际类型参数</li>
 *   <li>类型替换：实现类型变量的替换和具体化（如将T替换为Integer）</li>
 *   <li>框架开发：在ORM、序列化框架中处理泛型类型变量</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 定义一个泛型类
 * class Example&lt;T extends Number&gt; {
 *     private T value;
 * }
 * 
 * // 获取类型变量T
 * TypeVariable&lt;Class&lt;Example&gt;&gt; typeVariable = Example.class.getTypeParameters()[0];
 * 
 * // 创建可解析的类型变量
 * ResolvableTypeVariable resolvableTypeVar = new ResolvableTypeVariable(
 *     typeVariable, 
 *     TypeVariableResolver.DEFAULT
 * );
 * 
 * // 获取类型变量的上界
 * ResolvableType[] bounds = resolvableTypeVar.getBounds();
 * System.out.println("类型变量T的上界: " + bounds[0].getTypeName()); // 输出: java.lang.Number
 * 
 * // 解析类型变量（假设已注册映射关系）
 * ResolvableType resolvedType = resolvableTypeVar.getSource();
 * System.out.println("解析后的实际类型: " + resolvedType.getTypeName()); // 可能输出: java.lang.Integer
 * </pre>
 *
 * @see AbstractResolvableType
 * @see ResolvableTypeWrapper
 * @see TypeVariable
 * @see TypeVariableResolver
 */
@Getter
public class ResolvableTypeVariable extends AbstractResolvableType<TypeVariable<?>>
        implements ResolvableTypeWrapper<ResolvableType> {

    /**
     * 创建可解析的类型变量解析器。
     * <p>
     * 该构造函数用于初始化类型变量解析器，关联类型变量和类型变量解析器。
     *
     * @param type 类型变量对象，不可为null
     * @param typeVariableResolver 类型变量解析器，可为null（表示使用默认解析器）
     */
    public ResolvableTypeVariable(@NonNull TypeVariable<?> type, TypeVariableResolver typeVariableResolver) {
        super(type, typeVariableResolver);
    }

    /**
     * 获取类型变量的边界约束（上界和下界）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>从TypeVariable中获取声明的边界类型数组</li>
     *   <li>使用当前类型变量解析器将边界类型转换为ResolvableType数组</li>
     * </ol>
     * 例如，对于类型变量T extends Number &amp; Comparable，返回[Number, Comparable]的ResolvableType数组。
     *
     * @return 类型变量的边界约束的ResolvableType数组
     */
    public ResolvableType[] getBounds() {
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getBounds());
    }

    /**
     * 获取类型变量解析后的实际类型。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>使用类型变量解析器尝试解析当前类型变量</li>
     *   <li>若解析成功，返回解析后的实际类型；否则返回NONE（表示未解析）</li>
     * </ol>
     * 例如，在泛型类List&lt;String&gt;中，类型变量E会被解析为String类型。
     *
     * @return 解析后的实际类型，若未解析则返回NONE
     */
    @Override
    public ResolvableType getSource() {
        ResolvableType resolvableType = getTypeVariableResolver() == null ? null
                : getTypeVariableResolver().resolveTypeVariable(getType());
        return resolvableType == null ? NONE : resolvableType;
    }

    /**
     * 获取类型变量的名称（解析后的或原始的）。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>调用getSource()获取解析后的实际类型</li>
     *   <li>若解析成功，返回实际类型的名称；否则返回原始类型变量的名称</li>
     * </ol>
     * 例如，在泛型类List&lt;String&gt;中，类型变量E的名称会被解析为"java.lang.String"；
     * 若未解析，则返回原始名称"E"。
     *
     * @return 解析后的类型名称或原始类型变量名称
     */
    @Override
    public String getTypeName() {
        ResolvableType resolved = getSource();
        if (resolved == null || resolved == NONE) {
            return getType().getTypeName();
        }
        return resolved.getTypeName();
    }

    /**
     * 获取类型变量的下界（等同于边界约束）。
     * <p>
     * 对于Java类型变量，其下界由边界约束定义，因此直接返回getBounds()的结果。
     *
     * @return 类型变量的下界约束的ResolvableType数组
     */
    @Override
    public final ResolvableType[] getLowerBounds() {
        return getBounds();
    }

    /**
     * 获取类型变量的上界（等同于边界约束）。
     * <p>
     * 对于Java类型变量，其上界由边界约束定义，因此直接返回getBounds()的结果。
     *
     * @return 类型变量的上界约束的ResolvableType数组
     */
    @Override
    public final ResolvableType[] getUpperBounds() {
        return getBounds();
    }
}