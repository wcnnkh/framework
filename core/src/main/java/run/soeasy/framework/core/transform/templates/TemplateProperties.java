package run.soeasy.framework.core.transform.templates;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 模板属性映射适配器，通过函数式转换实现源模板元素到目标类型的动态映射，
 * 实现{@link TemplateMapping}接口以支持键/索引双向访问和集合形式转换。
 * <p>
 * 该类采用装饰器模式包装源模板实例，所有操作委托给源模板并在值访问时应用映射函数，
 * 确保源模板与目标类型的转换逻辑解耦，同时保持数据访问的透明性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型转换：通过{@code mapper}函数实现{@code S -> V}的类型映射</li>
 *   <li>延迟转换：仅在元素访问时执行转换，避免预加载带来的资源消耗</li>
 *   <li>空值安全：自动处理源值为{@code null}的情况，防止NPE</li>
 *   <li>接口兼容：完全实现{@link TemplateMapping}接口，支持数组/Map形式转换</li>
 * </ul>
 *
 * <p><b>泛型约束：</b>
 * <ul>
 *   <li>{@code S}：源模板元素类型，需实现{@link AccessibleDescriptor}以支持类型化访问</li>
 *   <li>{@code T}：源模板类型，需实现{@link Template}接口</li>
 *   <li>{@code V}：目标元素类型，需实现{@link TypedValueAccessor}以支持值操作</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TemplateMapping
 * @see AccessibleDescriptor
 * @see TypedValueAccessor
 */
@RequiredArgsConstructor
@Getter
public class TemplateProperties<S extends AccessibleDescriptor, T extends Template<S>, V extends TypedValueAccessor>
        implements TemplateMapping<V> {
    
    /** 被包装的源模板实例，不可为null */
    @NonNull
    private final T template;
    
    /** 元素类型转换函数，不可为null，负责{@code S -> V}的类型映射 */
    @NonNull
    private final Function<? super S, ? extends V> mapper;

    /**
     * 获取所有元素的键值对集合，对每个元素应用映射函数转换类型
     * <p>
     * 若源模板元素集合为{@code null}，直接返回{@code null}；
     * 否则通过{@code map}操作对每个元素执行类型转换。
     * 
     * @return 转换后的键值对集合，或{@code null}
     */
    @Override
    public Elements<KeyValue<Object, V>> getElements() {
        Elements<KeyValue<Object, S>> elements = template.getElements();
        if (elements == null) {
            return null;
        }

        return elements.map((e) -> KeyValue.of(e.getKey(), e.getValue() == null ? null : mapper.apply(e.getValue())));
    }

    /**
     * 根据键获取唯一元素，应用映射函数转换类型
     * <p>
     * 调用源模板的{@code get(key)}方法获取源元素，若存在则执行类型转换；
     * 若键对应多个元素，抛出{@link NoUniqueElementException}。
     * 
     * @param key 查找键，支持数值索引或键名
     * @return 转换后的目标元素，或{@code null}（键不存在时）
     * @throws NoUniqueElementException 当键对应多个源元素时抛出
     */
    @Override
    public V get(Object key) throws NoUniqueElementException {
        S value = template.get(key);
        return value == null ? null : mapper.apply(value);
    }

    /**
     * 获取模板元素数量，直接委托给源模板
     * 
     * @return 源模板的元素数量
     */
    @Override
    public int size() {
        return template.size();
    }

    /**
     * 判断模板是否包含指定键，直接委托给源模板
     * 
     * @param key 待检查的键
     * @return 包含返回{@code true}，否则{@code false}
     */
    @Override
    public boolean hasKey(Object key) {
        return template.hasKey(key);
    }

    /**
     * 判断模板是否包含元素，直接委托给源模板
     * 
     * @return 包含元素返回{@code true}，否则{@code false}
     */
    @Override
    public boolean hasElements() {
        return template.hasElements();
    }

    /**
     * 根据索引获取元素键值对，应用映射函数转换类型
     * <p>
     * 若索引超出范围返回{@code null}，否则对源元素执行类型转换。
     * 
     * @param index 元素索引
     * @return 转换后的键值对，或{@code null}（索引无效时）
     */
    @Override
    public KeyValue<Object, V> getElement(int index) {
        KeyValue<Object, S> keyValue = template.getElement(index);
        return keyValue == null ? null
                : KeyValue.of(keyValue.getKey(),
                        keyValue.getValue() == null ? null : mapper.apply(keyValue.getValue()));
    }

    /**
     * 判断模板是否为数组形式，直接委托给源模板
     * 
     * @return 是数组返回{@code true}，否则{@code false}
     */
    @Override
    public boolean isArray() {
        return template.isArray();
    }

    /**
     * 判断模板是否为Map形式，直接委托给源模板
     * 
     * @return 是Map返回{@code true}，否则{@code false}
     */
    @Override
    public boolean isMap() {
        return template.isMap();
    }

    /**
     * 获取所有键的集合，直接委托给源模板
     * 
     * @return 键的集合
     */
    @Override
    public Elements<Object> keys() {
        return template.keys();
    }

    /**
     * 根据键获取所有匹配的值，应用映射函数转换类型
     * <p>
     * 若键不存在返回{@code null}，否则对源值集合执行批量转换。
     * 
     * @param key 查找键
     * @return 转换后的目标值集合，或{@code null}（键不存在时）
     */
    @Override
    public Elements<V> getValues(Object key) {
        Elements<S> elements = template.getValues(key);
        return elements == null ? null : elements.map(mapper);
    }
}