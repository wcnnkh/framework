package run.soeasy.framework.core.mapping.property;

/**
 * 对象模板工厂接口，定义创建和获取对象属性模板的标准方法，
 * 用于将Java类的结构信息转换为框架内部的属性模板表示。
 * <p>
 * 该接口提供对象类与属性模板之间的映射关系，支持查询和获取特定类的属性模板，
 * 适用于需要动态生成或缓存对象属性结构的场景，如对象映射、数据绑定等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>模板创建：根据对象类动态生成或获取对应的属性模板</li>
 *   <li>存在性检查：通过{@link #hasObjectTemplate(Class)}方法快速判断是否存在模板</li>
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
 *   <li>对象映射框架：自动识别对象属性并生成映射模板</li>
 *   <li>数据绑定：将外部数据（如JSON、XML）映射到对象属性</li>
 *   <li>ORM框架：生成对象与数据库字段的映射关系</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see Property
 */
public interface ObjectTemplateFactory<E extends Property> {
    
    /**
     * 检查是否存在指定对象类的属性模板
     * <p>
     * 该默认实现通过调用{@link #getObjectTemplate(Class)}方法并检查返回值是否为null来判断，
     * 子类可根据需要重写此方法以提供更高效的存在性检查逻辑。
     * 
     * @param objectClass 对象类，不可为null
     * @return 若存在对应的属性模板返回true，否则返回false
     */
    default boolean hasObjectTemplate(Class<?> objectClass) {
        return getObjectTemplate(objectClass) != null;
    }

    /**
     * 获取指定对象类的属性模板
     * <p>
     * 实现类应根据对象类的结构信息（如字段、方法）生成对应的属性模板，
     * 或从缓存中获取已存在的模板。返回的模板应包含对象类的所有可映射属性。
     * 
     * @param objectClass 对象类，不可为null
     * @return 对应的属性模板，若不存在则返回null
     */
    PropertyMapping<E> getObjectTemplate(Class<?> objectClass);
}