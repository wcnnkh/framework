package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;
import java.util.Map;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可解析类型包装器接口，用于将任意类型包装为可解析类型（{@link ResolvableType}），
 * 继承自{@link ResolvableType}和{@link Wrapper}接口。
 * 该接口通过委派模式将所有方法调用转发给被包装的源类型，
 * 适用于需要增强或代理现有类型解析功能的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型包装：将任意可解析类型（如Class、TypeVariable等）包装为统一接口</li>
 *   <li>方法委派：默认实现将所有方法调用转发给被包装的源类型</li>
 *   <li>功能增强：允许在不修改原类型的情况下添加额外行为</li>
 *   <li>泛型支持：使用泛型参数限定被包装的源类型必须实现ResolvableType</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>类型转换：将非标准的类型表示转换为统一的ResolvableType接口</li>
 *   <li>代理增强：在类型解析过程中添加缓存、日志等额外功能</li>
 *   <li>框架集成：将第三方类型系统集成到框架的类型体系中</li>
 *   <li>适配器模式：适配不同来源的类型信息</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建一个需要包装的原始类型
 * ResolvableType originalType = ResolvableType.forClass(List.class);
 * 
 * // 创建类型包装器
 * ResolvableTypeWrapper&lt;ResolvableType&gt; wrappedType = new ResolvableTypeWrapper&lt;&gt;() {
 *     public ResolvableType getSource() {
 *         return originalType;
 *     }
 * };
 * 
 * // 使用包装后的类型
 * System.out.println("原始类型: " + wrappedType.getRawType()); // 输出: interface java.util.List
 * System.out.println("是否为数组: " + wrappedType.isArray()); // 输出: false
 * </pre>
 *
 * @param <W> 被包装的源类型，必须实现ResolvableType接口
 * @see ResolvableType
 * @see Wrapper
 */
@FunctionalInterface
public interface ResolvableTypeWrapper<W extends ResolvableType> extends ResolvableType, Wrapper<W> {
    
    /**
     * 获取被包装的源类型。
     * <p>
     * 所有方法调用将委派给该源类型执行。
     *
     * @return 被包装的源类型
     */
    @Override
    W getSource();

    /**
     * 获取实际类型参数（委派给源类型）。
     * <p>
     * 例如，对于List&lt;String&gt;类型，返回包含String的ResolvableType数组。
     *
     * @return 实际类型参数的ResolvableType数组
     */
    @Override
    default ResolvableType[] getActualTypeArguments() {
        return getSource().getActualTypeArguments();
    }

    /**
     * 获取数组组件类型（委派给源类型）。
     * <p>
     * 例如，对于String[]类型，返回String的ResolvableType表示。
     *
     * @return 数组组件类型的ResolvableType，非数组类型返回null
     */
    @Override
    default ResolvableType getComponentType() {
        return getSource().getComponentType();
    }

    /**
     * 获取类型变量的下界（委派给源类型）。
     * <p>
     * 例如，对于类型变量T super Integer，返回Integer的ResolvableType表示。
     *
     * @return 类型变量下界的ResolvableType数组
     */
    @Override
    default ResolvableType[] getLowerBounds() {
        return getSource().getLowerBounds();
    }

    /**
     * 获取内部类的外部类型（委派给源类型）。
     * <p>
     * 例如，对于OuterClass.InnerClass类型，返回OuterClass的ResolvableType表示。
     *
     * @return 外部类型的ResolvableType，无外部类型返回null
     */
    @Override
    default ResolvableType getOwnerType() {
        return getSource().getOwnerType();
    }

    /**
     * 获取原始类型（委派给源类型）。
     * <p>
     * 例如，对于List&lt;String&gt;类型，返回List.class。
     *
     * @return 原始类型的Class对象
     */
    @Override
    default Class<?> getRawType() {
        return getSource().getRawType();
    }

    /**
     * 获取类型变量的上界（委派给源类型）。
     * <p>
     * 例如，对于类型变量T extends Number，返回Number的ResolvableType表示。
     *
     * @return 类型变量上界的ResolvableType数组
     */
    @Override
    default ResolvableType[] getUpperBounds() {
        return getSource().getUpperBounds();
    }

    /**
     * 判断是否包含实际类型参数（委派给源类型）。
     * <p>
     * 例如，List&lt;String&gt;包含实际类型参数，而List不包含。
     *
     * @return true如果包含实际类型参数，否则false
     */
    @Override
    default boolean hasActualTypeArguments() {
        return getSource().hasActualTypeArguments();
    }

    /**
     * 判断是否为数组类型（委派给源类型）。
     * <p>
     * 例如，String[]返回true，List返回false。
     *
     * @return true如果是数组类型，否则false
     */
    @Override
    default boolean isArray() {
        return getSource().isArray();
    }

    /**
     * 解析类型变量（委派给源类型）。
     * <p>
     * 在泛型上下文中，将类型变量解析为实际类型。
     *
     * @param typeVariable 待解析的类型变量
     * @return 解析后的实际类型的ResolvableType表示
     */
    @Override
    default ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
        return getSource().resolveTypeVariable(typeVariable);
    }

    /**
     * 将当前类型转换为指定类型（委派给源类型）。
     * <p>
     * 例如，将List&lt;String&gt;转换为Collection类型。
     *
     * @param type 目标类型
     * @return 转换后的ResolvableType表示
     */
    @Override
    default ResolvableType as(Class<?> type) {
        return getSource().as(type);
    }

    /**
     * 获取指定索引的实际类型参数（委派给源类型）。
     * <p>
     * 例如，对于Map&lt;String, Integer&gt;，getActualTypeArgument(0)返回String的ResolvableType表示。
     *
     * @param indexes 嵌套索引
     * @return 指定索引的实际类型参数的ResolvableType表示
     */
    @Override
    default ResolvableType getActualTypeArgument(int... indexes) {
        return getSource().getActualTypeArgument(indexes);
    }

    /**
     * 获取类型实现的接口（委派给源类型）。
     * <p>
     * 例如，List类型实现了Collection、Iterable等接口。
     *
     * @return 接口的ResolvableType数组
     */
    @Override
    default ResolvableType[] getInterfaces() {
        return getSource().getInterfaces();
    }

    /**
     * 获取嵌套层级的类型（委派给源类型）。
     * <p>
     * 例如，对于List&lt;Map&lt;String, Integer&gt;&gt;，getNested(2)返回Integer的ResolvableType表示。
     *
     * @param nestingLevel 嵌套层级
     * @return 嵌套层级的ResolvableType表示
     */
    @Override
    default ResolvableType getNested(int nestingLevel) {
        return getSource().getNested(nestingLevel);
    }

    /**
     * 获取嵌套层级的类型（委派给源类型）。
     * <p>
     * 例如，对于List&lt;Map&lt;String, Integer&gt;&gt;，getNested(2, Map.of(1, 0))返回String的ResolvableType表示。
     *
     * @param nestingLevel 嵌套层级
     * @param typeIndexesPerLevel 每层的类型参数索引映射
     * @return 嵌套层级的ResolvableType表示
     */
    @Override
    default ResolvableType getNested(int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
        return getSource().getNested(nestingLevel, typeIndexesPerLevel);
    }

    /**
     * 获取父类类型（委派给源类型）。
     * <p>
     * 例如，ArrayList的父类是AbstractList。
     *
     * @return 父类的ResolvableType表示
     */
    @Override
    default ResolvableType getSuperType() {
        return getSource().getSuperType();
    }

    /**
     * 获取类型名称（委派给源类型）。
     * <p>
     * 例如，List&lt;String&gt;的类型名称是"java.util.List&lt;java.lang.String&gt;"。
     *
     * @return 类型名称
     */
    @Override
    default String getTypeName() {
        return getSource().getTypeName();
    }

    /**
     * 判断当前类型是否可从另一个类型赋值（委派给源类型）。
     * <p>
     * 例如，List&lt;String&gt;可从ArrayList&lt;String&gt;赋值。
     *
     * @param other 另一个类型
     * @return true如果可赋值，否则false
     */
    @Override
    default boolean isAssignableFrom(ResolvableType other) {
        return getSource().isAssignableFrom(other);
    }
}