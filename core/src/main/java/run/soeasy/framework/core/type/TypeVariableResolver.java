package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;

/**
 * 类型变量解析器，用于将Java反射中的{@link TypeVariable}解析为具体的{@link ResolvableType}。
 * 该接口在泛型类型解析中起核心作用，负责将泛型类型变量（如T、E）映射到实际类型。
 *
 * <p>核心功能：
 * <ul>
 *   <li>类型变量解析：将TypeVariable解析为具体的ResolvableType</li>
 *   <li>上下文关联：在特定上下文中解析类型变量（如类、方法）</li>
 *   <li>泛型信息保留：在反射过程中保留泛型类型信息</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型类型解析：在运行时解析泛型类或方法中的类型变量</li>
 *   <li>反射操作：在反射调用中处理泛型参数和返回值</li>
 *   <li>框架开发：在ORM、序列化框架中处理泛型类型信息</li>
 *   <li>类型安全转换：确保泛型类型变量在运行时的类型安全性</li>
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
 * // 创建类型变量解析器
 * TypeVariableResolver resolver = new TypeVariableResolver() {
 *     
 *     public ResolvableType resolveTypeVariable(TypeVariable&lt;?&gt; tv) {
 *         if (tv.getName().equals("T")) {
 *             return ResolvableType.forClass(Integer.class);
 *         }
 *         return null;
 *     }
 * };
 * 
 * // 解析类型变量
 * ResolvableType resolvedType = resolver.resolveTypeVariable(typeVariable);
 * System.out.println("解析后的类型: " + resolvedType.getTypeName()); // 输出: java.lang.Integer
 * </pre>
 *
 * @see TypeVariable
 * @see ResolvableType
 */
@FunctionalInterface
public interface TypeVariableResolver {
    
    /**
     * 将类型变量解析为具体的可解析类型。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>根据TypeVariable的声明位置（类、方法）确定解析上下文</li>
     *   <li>查找TypeVariable对应的实际类型参数（如果有）</li>
     *   <li>将TypeVariable转换为对应的ResolvableType</li>
     * </ol>
     *
     * @param typeVariable 需要解析的类型变量（如T、E）
     * @return 解析后的可解析类型，若无法解析则返回null
     */
    ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable);
}