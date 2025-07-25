package run.soeasy.framework.core.transform.property;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateProperties;

/**
 * 属性模板属性集合，继承自{@link TemplateProperties}并实现{@link PropertyMapping}，
 * 用于将属性描述符集合转换为属性访问器集合，支持通过索引或名称访问属性。
 * <p>
 * 该类采用装饰器模式包装属性模板，通过映射函数将每个属性描述符转换为属性访问器，
 * 适用于需要统一访问和操作属性的场景，如对象属性映射、数据绑定等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>描述符到访问器的映射：通过提供的映射函数将属性描述符转换为属性访问器</li>
 *   <li>多方式访问：支持通过索引（{@link #get(int)}）或名称（{@link #get(String)}）访问属性</li>
 *   <li>流式操作：实现{@link Iterable}和{@link Stream}接口，支持lambda表达式和流式处理</li>
 *   <li>类型安全：通过泛型确保属性描述符和访问器的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code S}：属性描述符类型，需实现{@link PropertyDescriptor}</li>
 *   <li>{@code T}：属性模板类型，需实现{@link PropertyTemplate}</li>
 *   <li>{@code V}：属性访问器类型，需实现{@link PropertyAccessor}</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see TemplateProperties
 * @see PropertyDescriptor
 * @see PropertyAccessor
 */
public class PropertyTemplateProperties<S extends PropertyDescriptor, T extends PropertyTemplate<S>, V extends PropertyAccessor>
        extends TemplateProperties<S, T, V> implements PropertyMapping<V> {

    /**
     * 构造属性模板属性集合
     * 
     * @param template 被包装的属性模板，不可为null
     * @param mapper 将属性描述符映射为属性访问器的函数，不可为null
     */
    public PropertyTemplateProperties(@NonNull T template, @NonNull Function<? super S, ? extends V> mapper) {
        super(template, mapper);
    }

    /**
     * 获取属性访问器的迭代器
     * <p>
     * 该迭代器会遍历属性模板中的所有描述符，
     * 并通过映射函数将每个描述符转换为对应的属性访问器。
     * 
     * @return 属性访问器的迭代器
     */
    @Override
    public Iterator<V> iterator() {
        Stream<V> stream = CollectionUtils.unknownSizeStream(getTemplate().iterator()).map(getMapper());
        return stream.iterator();
    }

    /**
     * 获取属性访问器的流视图
     * <p>
     * 该流会映射属性模板中的所有描述符为对应的属性访问器，
     * 支持惰性操作和并行处理。
     * 
     * @return 属性访问器的流
     */
    @Override
    public Stream<V> stream() {
        return getTemplate().stream().map(getMapper());
    }

    /**
     * 根据索引获取属性访问器
     * <p>
     * 通过索引从属性模板中获取描述符，
     * 并使用映射函数将其转换为对应的属性访问器。
     * 
     * @param index 索引位置
     * @return 对应位置的属性访问器，若索引超出范围则返回null
     * @throws IndexOutOfBoundsException 若索引为负数
     */
    @Override
    public V get(int index) throws IndexOutOfBoundsException {
        S value = getTemplate().get(index);
        return value == null ? null : getMapper().apply(value);
    }

    /**
     * 根据名称获取属性访问器
     * <p>
     * 通过名称从属性模板中获取唯一描述符，
     * 并使用映射函数将其转换为对应的属性访问器。
     * 
     * @param name 属性名称
     * @return 对应名称的属性访问器，若不存在则返回null
     * @throws NoUniqueElementException 若存在多个同名属性
     */
    @Override
    public V get(String name) throws NoUniqueElementException {
        S value = getTemplate().get(name);
        return value == null ? null : getMapper().apply(value);
    }

    /**
     * 获取所有属性访问器的类型数组
     * <p>
     * 该方法会遍历所有属性描述符，
     * 先将其转换为属性访问器，再通过类型映射函数获取类型，
     * 最终返回类型数组。
     * 
     * @param typeMapper 将属性访问器映射为类型的函数
     * @return 类型数组，顺序与属性顺序一致
     */
    @Override
    public Class<?>[] getTypes(Function<? super V, Class<?>> typeMapper) {
        return getTemplate().getTypes((e) -> typeMapper.apply(getMapper().apply(e)));
    }
}