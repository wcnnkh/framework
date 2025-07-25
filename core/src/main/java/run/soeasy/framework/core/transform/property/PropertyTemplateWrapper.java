package run.soeasy.framework.core.transform.property;

import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.templates.TemplateWrapper;

/**
 * 属性模板包装器接口，继承自{@link PropertyTemplate}、{@link TemplateWrapper}和{@link ElementsWrapper}，
 * 采用装饰器模式实现对目标属性模板的包装，支持在不修改原模板的前提下扩展其功能。
 * <p>
 * 该接口定义了属性模板包装的标准规范，默认方法将所有操作委托给被包装的源模板，
 * 子类可通过覆盖特定方法修改模板的检索逻辑、元素处理或结构转换行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装现有{@link PropertyTemplate}实例并扩展功能</li>
 *   <li>委托处理：默认操作转发给被包装的源模板，保持原有逻辑</li>
 *   <li>类型安全：通过泛型约束保证包装前后的类型一致性</li>
 *   <li>结构转换：支持将包装后的模板转换为Map或数组形式</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code T}：属性描述符类型，需实现{@link PropertyDescriptor}</li>
 *   <li>{@code W}：被包装的源属性模板类型，需实现{@link PropertyTemplate}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性模板的元数据过滤（如按名称筛选属性）</li>
 *   <li>模板结构转换的统一控制（如强制转换为Map形式）</li>
 *   <li>属性访问的日志记录或监控</li>
 *   <li>属性类型的动态转换（如包装时修改属性类型描述）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyTemplate
 * @see TemplateWrapper
 * @see ElementsWrapper
 */
public interface PropertyTemplateWrapper<T extends PropertyDescriptor, W extends PropertyTemplate<T>>
        extends PropertyTemplate<T>, TemplateWrapper<T, W>, ElementsWrapper<T, W> {
    
    /**
     * 根据键获取属性描述符（委托给源模板）
     * <p>
     * 该默认实现调用被包装源模板的{@link PropertyTemplate#get(Object)}方法，
     * 子类可覆盖此方法实现自定义检索逻辑（如缓存、过滤）。
     * 
     * @param key 检索键，支持数字、字符串或其他类型
     * @return 匹配的属性描述符
     * @throws NoUniqueElementException 当键对应多个属性时抛出
     */
    @Override
    default T get(Object key) throws NoUniqueElementException {
        return getSource().get(key);
    }

    /**
     * 根据属性名称获取唯一属性描述符（委托给源模板）
     * <p>
     * 该默认实现调用被包装源模板的{@link PropertyTemplate#get(String)}方法，
     * 子类可覆盖此方法实现名称匹配规则的修改（如大小写不敏感匹配）。
     * 
     * @param name 属性名称
     * @return 匹配的属性描述符
     * @throws NoUniqueElementException 当存在多个同名属性时抛出
     */
    default T get(String name) {
        return getSource().get(name);
    }

    /**
     * 获取所有属性的键值对集合（键为属性名称）
     * <p>
     * 将每个属性转换为{@link KeyValue}，键为属性名称，值为属性描述符，
     * 该实现与源模板逻辑一致，子类可覆盖以修改键的生成规则。
     * 
     * @return 键值对元素集合
     */
    @Override
    default Elements<KeyValue<Object, T>> getElements() {
        return Elements.of(() -> stream().map((e) -> KeyValue.of(e.getName(), e)));
    }

    /**
     * 获取属性类型数组（委托给源模板）
     * <p>
     * 该默认实现调用被包装源模板的{@link PropertyTemplate#getTypes(Function)}方法，
     * 子类可覆盖此方法实现类型映射逻辑的修改（如添加类型转换）。
     * 
     * @param typeMapper 类型映射函数，输入属性描述符，输出类型对象
     * @return 类型数组，顺序与属性顺序一致
     */
    default Class<?>[] getTypes(Function<? super T, Class<?>> typeMapper) {
        return getSource().getTypes(typeMapper);
    }

    /**
     * 获取属性的流视图
     * <p>
     * 使用{@link CollectionUtils}创建未知大小的流，支持惰性操作，
     * 该实现与源模板逻辑一致，子类可覆盖以修改流的处理逻辑。
     * 
     * @return 属性流
     */
    @Override
    default Stream<T> stream() {
        return CollectionUtils.unknownSizeStream(this.iterator());
    }

    /**
     * 转换为Map形式的属性模板（委托给源模板）
     * <p>
     * 该默认实现调用被包装源模板的{@link PropertyTemplate#asMap(boolean)}方法，
     * 子类可覆盖此方法自定义Map模板的创建逻辑。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的属性模板
     */
    @Override
    default PropertyTemplate<T> asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 转换为数组形式的属性模板（委托给源模板）
     * <p>
     * 该默认实现调用被包装源模板的{@link PropertyTemplate#asArray(boolean)}方法，
     * 子类可覆盖此方法自定义数组模板的创建逻辑。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的属性模板
     */
    @Override
    default PropertyTemplate<T> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }
}