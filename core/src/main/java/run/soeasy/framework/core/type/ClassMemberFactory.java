package run.soeasy.framework.core.type;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 类成员工厂接口，定义创建类成员提供者的标准方法。
 * 该接口允许框架或应用根据需要实现不同的类成员获取策略，
 * 例如获取类的方法、字段、注解等成员信息。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型抽象：通过泛型参数T支持不同类型的类成员（如Method、Field等）</li>
 *   <li>提供者模式：返回{@link Provider}接口实现，支持延迟加载和懒初始化</li>
 *   <li>声明类识别：根据指定的声明类获取对应的成员提供者</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射工具：集中管理类成员的获取逻辑</li>
 *   <li>ORM框架：获取实体类的字段和方法信息</li>
 *   <li>依赖注入：分析类的构造函数和属性注入点</li>
 *   <li>注解处理：扫描类上的注解信息</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建一个获取类方法的工厂
 * ClassMemberFactory&lt;Method&gt; methodFactory = new MethodMemberFactory();
 * 
 * // 获取User类的方法提供者
 * Provider&lt;Method&gt; methodProvider = methodFactory.getClassMemberProvider(User.class);
 * 
 * // 遍历所有方法
 * for (Method method : methodProvider) {
 *     System.out.println("方法: " + method.getName());
 * }
 * </pre>
 *
 * @param <T> 类成员的类型（如Method、Field等）
 * @see Provider
 */
public interface ClassMemberFactory<T> {
    /**
     * 获取指定声明类的成员提供者。
     * <p>
     * 该方法返回的提供者可用于获取声明类中定义的特定类型成员（如方法、字段等）。
     * 实现类应确保返回的提供者是线程安全的，特别是在高并发环境下。
     *
     * @param declaringClass 声明成员的类，不可为null
     * @return 类成员提供者
     */
    Provider<T> getClassMemberProvider(@NonNull Class<?> declaringClass);
}