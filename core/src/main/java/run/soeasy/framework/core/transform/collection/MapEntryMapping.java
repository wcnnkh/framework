package run.soeasy.framework.core.transform.collection;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.templates.TemplateMapping;

/**
 * Map条目模板映射实现，实现{@link TemplateMapping}接口，
 * 用于将Map的键值对映射为类型化值访问器（{@link TypedValueAccessor}），
 * 支持类型安全的Map条目访问和操作。
 * <p>
 * 该类通过包装Map实例和类型描述符，将Map的每个条目转换为独立的访问器，
 * 适用于需要将Map视为属性集合进行操作的场景，如表单数据绑定、动态属性访问等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全访问：通过{@link TypeDescriptor}确保Map值的类型一致性</li>
 *   <li>统一访问接口：将Map条目转换为{@link TypedValueAccessor}，提供一致的访问方式</li>
 *   <li>自动类型转换：使用{@link Converter}实现Map值的类型转换</li>
 *   <li>高效遍历：支持通过流(Stream)高效遍历Map的所有条目</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>表单数据与Map之间的双向绑定</li>
 *   <li>动态属性访问（如配置项的读取与修改）</li>
 *   <li>Map数据与对象属性的映射转换</li>
 *   <li>需要类型安全操作Map条目的场景</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see TemplateMapping
 * @see TypedValueAccessor
 * @see MapEntryAccessor
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = "map")
public class MapEntryMapping implements TemplateMapping<TypedValueAccessor> {
    
    /** 被映射的Map实例，不可为null */
    @NonNull
    private final Map<?, ?> map;
    
    /** Map的类型描述符，用于获取值的类型信息，不可为null */
    @NonNull
    private final TypeDescriptor typeDescriptor;
    
    /** 类型转换器，用于Map值的类型转换，默认为系统转换服务 */
    private Converter converter = SystemConversionService.getInstance();

    /**
     * 根据键获取对应的类型化值访问器
     * <p>
     * 若Map包含指定键，则创建并返回对应的{@link MapEntryAccessor}，
     * 否则返回null。访问器可用于类型安全地读取和修改Map条目值。
     * </p>
     * 
     * @param key 要获取的Map键
     * @return 对应的类型化值访问器，若键不存在则返回null
     */
    @Override
    public TypedValueAccessor get(Object key) {
        return map.containsKey(key) ? createAccessor(key) : null;
    }

    /**
     * 判断Map是否包含指定键
     * 
     * @param key 要检查的Map键
     * @return 若Map包含指定键返回true，否则返回false
     */
    @Override
    public boolean hasKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * 获取所有Map条目的键值对集合
     * <p>
     * 将Map的每个条目转换为{@link KeyValue}对象，其中值部分为对应的
     * 类型化值访问器，支持通过流(Stream)进行高效遍历。
     * </p>
     * 
     * @return 包含所有Map条目的KeyValue集合
     */
    @Override
    public Elements<KeyValue<Object, TypedValueAccessor>> getElements() {
        return Elements.of(() -> map.keySet().stream().map((key) -> KeyValue.of(key, createAccessor(key))));
    }

    /**
     * 根据键获取对应的类型化值访问器集合
     * <p>
     * 若Map包含指定键，返回包含对应访问器的单元素集合；
     * 否则返回空集合。
     * </p>
     * 
     * @param key 要获取的Map键
     * @return 包含对应访问器的集合
     */
    @Override
    public Elements<TypedValueAccessor> getValues(Object key) {
        TypedValueAccessor indexed = get(key);
        return indexed == null ? Elements.empty() : Elements.singleton(indexed);
    }

    /**
     * 创建Map条目访问器
     * <p>
     * 使用提供的Map、键、类型描述符和转换器创建{@link MapEntryAccessor}实例，
     * 用于类型安全地访问和操作Map条目值。
     * </p>
     * 
     * @param key Map中的键
     * @return 新创建的Map条目访问器
     */
    private TypedValueAccessor createAccessor(Object key) {
        return new MapEntryAccessor(map, key, typeDescriptor, converter);
    }
}