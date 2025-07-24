package run.soeasy.framework.core.transform.templates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.soeasy.framework.core.attribute.SimpleAttributes;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 映射上下文类，用于维护映射操作中的上下文状态，支持嵌套上下文结构。
 * <p>
 * 该类继承自{@link SimpleAttributes}，可存储键值对属性，同时维护当前映射、
 * 键值对及父级上下文引用，适用于需要跟踪映射过程中状态的场景，如对象转换、
 * 数据映射等复杂操作的上下文管理。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>属性存储：继承自SimpleAttributes，支持动态属性存储</li>
 *   <li>上下文嵌套：通过parent字段支持多级上下文嵌套</li>
 *   <li>映射状态维护：持有当前映射和键值对引用</li>
 *   <li>上下文创建：提供创建当前上下文和嵌套上下文的工厂方法</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>内存泄漏风险：深度嵌套的上下文可能导致长生命周期对象引用</li>
 *   <li>线程安全：非线程安全实现，多线程环境需外部同步</li>
 *   <li>泛型复杂性：多层泛型参数可能增加使用和维护难度</li>
 *   <li>空值处理：未对mapping和keyValue进行严格空值校验</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see SimpleAttributes
 * @see Mapping
 * @see KeyValue
 */
@Getter
@AllArgsConstructor
public class MappingContext<K, V extends TypedValueAccessor, T extends Mapping<K, V>> 
        extends SimpleAttributes<String, Object> {
    
    /** 父级上下文引用，形成上下文链 */
    private final MappingContext<K, V, T> parent;
    
    /** 当前映射实例，不可为null */
    private final T mapping;
    
    /** 当前键值对实例，可为null */
    private final KeyValue<K, V> keyValue;

    /**
     * 构造基本映射上下文
     * 
     * @param template 映射模板实例
     */
    public MappingContext(T template) {
        this(template, null);
    }

    /**
     * 构造带键值对的映射上下文
     * 
     * @param template 映射模板实例
     * @param keyValue 当前键值对
     */
    public MappingContext(T template, KeyValue<K, V> keyValue) {
        this(null, template, keyValue);
    }

    /**
     * 判断是否存在有效映射
     * 
     * @return 存在映射实例返回true，否则false
     */
    public boolean hasMapping() {
        return mapping != null;
    }

    /**
     * 判断是否存在有效键值对
     * 
     * @return 存在键值对实例返回true，否则false
     */
    public boolean hasKeyValue() {
        return keyValue != null;
    }

    /**
     * 创建当前上下文（替换键值对）
     * <p>
     * 创建新上下文，使用当前映射和指定键值对，
     * 父级上下文保持不变。
     * 
     * @param keyValue 新键值对
     * @return 新的映射上下文实例
     */
    public final MappingContext<K, V, T> current(KeyValue<K, V> keyValue) {
        return current(null, keyValue);
    }

    /**
     * 创建当前上下文（替换映射和键值对）
     * <p>
     * 创建新上下文，可指定新映射和键值对，
     * 父级上下文保持不变。
     * 
     * @param mapping 新映射实例（null时使用当前映射）
     * @param keyValue 新键值对
     * @return 新的映射上下文实例
     */
    public MappingContext<K, V, T> current(T mapping, KeyValue<K, V> keyValue) {
        return new MappingContext<>(this.parent, mapping == null ? this.mapping : mapping, keyValue);
    }

    /**
     * 创建嵌套上下文（替换键值对）
     * <p>
     * 创建新上下文，父级设为当前上下文，
     * 使用当前映射和指定键值对。
     * 
     * @param keyValue 新键值对
     * @return 新的嵌套映射上下文实例
     */
    public final MappingContext<K, V, T> nested(KeyValue<K, V> keyValue) {
        return nested(null, keyValue);
    }

    /**
     * 创建嵌套上下文（替换映射和键值对）
     * <p>
     * 创建新上下文，父级设为当前上下文，
     * 可指定新映射和键值对。
     * 
     * @param mapping 新映射实例（null时使用当前映射）
     * @param keyValue 新键值对
     * @return 新的嵌套映射上下文实例
     */
    public MappingContext<K, V, T> nested(T mapping, KeyValue<K, V> keyValue) {
        return new MappingContext<>(this, mapping == null ? this.mapping : mapping, keyValue);
    }
}