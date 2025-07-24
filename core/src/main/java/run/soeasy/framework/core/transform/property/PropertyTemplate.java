package run.soeasy.framework.core.transform.property;

import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.templates.Template;

/**
 * 属性模板接口，作为函数式接口继承自{@link Template}和{@link Elements}，
 * 提供属性描述符的模板化管理和流式访问能力，支持通过键或名称检索属性描述符。
 * <p>
 * 该接口整合了模板的结构化访问与集合的流式操作特性，适用于属性元数据的集中管理场景，
 * 如对象属性映射、数据模型转换、动态属性访问等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>多维度检索：支持通过数字索引、字符串名称或泛型键检索属性</li>
 *   <li>流式操作：继承{@link Elements}的流式处理能力，支持lambda表达式</li>
 *   <li>结构转换：可转换为Map或数组形式的模板实现</li>
 *   <li>函数式支持：作为函数式接口可通过lambda表达式创建实例</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code T}：属性描述符类型，需实现{@link PropertyDescriptor}</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Template
 * @see Elements
 * @see PropertyDescriptor
 */
@FunctionalInterface
public interface PropertyTemplate<T extends PropertyDescriptor> extends Template<T>, Elements<T> {
    
    /**
     * 根据键获取属性描述符（支持多类型键）
     * <p>
     * 自动识别键类型：
     * <ul>
     *   <li>数字类型：作为索引获取对应位置的属性</li>
     *   <li>字符串类型：作为名称精确匹配属性</li>
     *   <li>其他类型：委托给{@link Template#get(Object)}处理</li>
     * </ul>
     * 
     * @param key 检索键，支持数字、字符串或其他类型
     * @return 匹配的属性描述符
     * @throws NoUniqueElementException 当键对应多个属性时抛出
     */
    @Override
    default T get(Object key) throws NoUniqueElementException {
        if (key instanceof Number) {
            return get(((Number) key).intValue());
        } else if (key instanceof String) {
            return get((String) key);
        }
        return Template.super.get(key);
    }

    /**
     * 根据属性名称获取唯一属性描述符
     * <p>
     * 通过名称精确匹配属性，若存在多个匹配项则抛出异常
     * </p>
     * 
     * @param name 属性名称
     * @return 匹配的属性描述符
     * @throws NoUniqueElementException 当存在多个同名属性时抛出
     */
    default T get(String name) throws NoUniqueElementException {
        return filter((e) -> StringUtils.equals(name, e.getName())).getUnique();
    }

    /**
     * 获取所有属性的键值对集合（键为属性名称）
     * <p>
     * 将每个属性转换为{@link KeyValue}，键为属性名称，值为属性描述符
     * </p>
     * 
     * @return 键值对元素集合
     */
    @Override
    default Elements<KeyValue<Object, T>> getElements() {
        return Elements.of(() -> stream().map((e) -> KeyValue.of(e.getName(), e)));
    }

    /**
     * 获取属性类型数组
     * <p>
     * 通过函数映射将每个属性转换为类型对象，形成类型数组
     * </p>
     * 
     * @param typeMapper 类型映射函数，输入属性描述符，输出类型对象
     * @return 类型数组，顺序与属性顺序一致
     */
    default Class<?>[] getTypes(Function<? super T, Class<?>> typeMapper) {
        Class<?>[] types = new Class<?>[size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = typeMapper.apply(get(i));
        }
        return types;
    }

    /**
     * 获取属性的流视图
     * <p>
     * 使用{@link CollectionUtils}创建未知大小的流，支持惰性操作
     * </p>
     * 
     * @return 属性流
     */
    @Override
    default Stream<T> stream() {
        return CollectionUtils.unknownSizeStream(this.iterator());
    }

    /**
     * 转换为Map形式的属性模板
     * <p>
     * 创建新的{@link MapPropertyTemplate}实例，可选择键唯一性约束
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的属性模板
     */
    @Override
    default PropertyTemplate<T> asMap(boolean uniqueness) {
        return new MapPropertyTemplate<>(this, uniqueness);
    }

    /**
     * 转换为数组形式的属性模板
     * <p>
     * 创建新的{@link ArrayPropertyTemplate}实例，可选择键唯一性约束
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的属性模板
     */
    @Override
    default PropertyTemplate<T> asArray(boolean uniqueness) {
        return new ArrayPropertyTemplate<>(this, uniqueness);
    }
}