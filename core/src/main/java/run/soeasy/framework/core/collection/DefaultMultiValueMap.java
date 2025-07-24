package run.soeasy.framework.core.collection;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 默认的多值映射实现，用于存储一个键对应多个值的映射关系。
 * 该类继承自AbstractMultiValueMap，通过包装底层Map实现多值映射功能。
 *
 * <p>设计特点：
 * <ul>
 *   <li>使用组合模式，封装任意Map实现（如HashMap、LinkedHashMap等）</li>
 *   <li>每个键对应一个List值，允许存储重复值</li>
 *   <li>提供符合Map接口规范的操作方法</li>
 *   <li>支持空值（null）作为键或值</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * MultiValueMap<String, String> map = new DefaultMultiValueMap<>(new HashMap<>());
 * map.add("key", "value1");
 * map.add("key", "value2");
 * List<String> values = map.get("key"); // 返回包含"value1"和"value2"的列表
 * }</pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <M> 底层Map实现的类型，必须继承Map<K, List<V>>
 * @see MultiValueMap
 * @see AbstractMultiValueMap
 */
@Getter
@RequiredArgsConstructor
public class DefaultMultiValueMap<K, V, M extends Map<K, List<V>>> extends AbstractMultiValueMap<K, V, M> {

    /** 被包装的底层Map实现 */
    @NonNull
    private final M source;
}