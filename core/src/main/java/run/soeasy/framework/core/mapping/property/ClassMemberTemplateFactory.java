package run.soeasy.framework.core.mapping.property;

import run.soeasy.framework.core.type.ClassMembersLoader;

/**
 * 类成员模板工厂接口，定义创建和获取类成员属性模板的标准方法，
 * 用于将Java类的成员信息（字段、方法等）转换为框架内部的属性模板表示。
 * <p>
 * 该接口提供类与类成员属性模板之间的映射关系，支持查询和获取特定类的成员属性模板，
 * 适用于需要动态分析和操作类成员的场景，如反射工具、ORM框架、代码生成器等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>模板创建：根据类动态生成或获取对应的类成员属性模板</li>
 *   <li>存在性检查：通过{@link #hasClassPropertyTemplate(Class)}方法快速判断是否存在模板</li>
 *   <li>泛型支持：通过泛型参数约束属性类型，保证类型一致性</li>
 *   <li>可扩展性：允许实现多种模板创建策略（如反射、注解、配置文件）</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code E}：属性类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射工具：动态获取和操作类的成员属性</li>
 *   <li>ORM框架：生成对象与数据库字段的映射关系</li>
 *   <li>代码生成器：根据类结构生成相关代码</li>
 *   <li>序列化框架：分析类成员以实现对象的序列化和反序列化</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ClassMembersLoader
 * @see Property
 */
public interface ClassMemberTemplateFactory<E extends Property> {
    
    /**
     * 检查是否存在指定类的成员属性模板
     * <p>
     * 该默认实现通过调用{@link #getClassPropertyTemplate(Class)}方法并检查返回值是否为null来判断，
     * 子类可根据需要重写此方法以提供更高效的存在性检查逻辑。
     * 
     * @param requiredClass 目标类，不可为null
     * @return 若存在对应的成员属性模板返回true，否则返回false
     */
    default boolean hasClassPropertyTemplate(Class<?> requiredClass) {
        return getClassPropertyTemplate(requiredClass) != null;
    }

    /**
     * 获取指定类的成员属性模板
     * <p>
     * 实现类应根据类的结构信息（如字段、方法）生成对应的成员属性模板，
     * 或从缓存中获取已存在的模板。返回的模板应包含类的所有可访问成员属性。
     * 
     * @param requriedClass 目标类，不可为null
     * @return 对应的成员属性模板，若不存在则返回null
     */
    ClassMembersLoader<E> getClassPropertyTemplate(Class<?> requriedClass);
}