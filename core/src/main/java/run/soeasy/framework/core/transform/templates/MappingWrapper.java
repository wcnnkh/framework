package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.DictionaryWrapper;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 映射包装器接口，继承自{@link Mapping}和{@link DictionaryWrapper}，
 * 用于包装基础映射实例并添加额外功能或修改行为，遵循包装器设计模式。
 * <p>
 * 该接口允许将一个映射实例包装为具有相同接口的新实例，从而在不修改原映射实现的前提下，
 * 实现功能增强（如日志记录、事务管理、权限控制等）。包装器会将所有操作委托给源映射实例，
 * 并可在委托前后添加自定义逻辑。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>包装器模式：通过{@link #getSource()}获取被包装的源映射实例</li>
 *   <li>透明代理：对外暴露与源映射一致的接口，客户端无感知</li>
 *   <li>功能增强：可在委托操作前后添加额外逻辑（如验证、转换等）</li>
 *   <li>集合转换：继承{@link Mapping}的集合转换方法，委托给源映射处理</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>空指针风险：若{@link #getSource()}返回null，所有操作将抛出NPE</li>
 *   <li>线程安全：未定义包装器的线程安全策略，依赖源映射的线程安全性</li>
 *   <li>性能损耗：每次操作均需经过包装器转发，可能带来额外开销</li>
 *   <li>类型擦除：泛型参数在运行时擦除，可能导致类型安全问题</li>
 *   <li>递归包装：多次包装同一映射可能导致嵌套过深，影响性能和调试</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，必须实现{@link TypedValueAccessor}
 * @param <W> 包装器自身的类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see Mapping
 * @see DictionaryWrapper
 * @see WrapperPattern
 */
@FunctionalInterface
public interface MappingWrapper<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
        extends Mapping<K, V>, DictionaryWrapper<K, V, KeyValue<K, V>, W> {

    /**
     * 将映射转换为数组形式（委托给源映射）
     * <p>
     * 该实现直接调用源映射的{@link Mapping#asArray(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的映射实例
     * @see Mapping#asArray(boolean)
     */
    @Override
    default Mapping<K, V> asArray(boolean uniqueness) {
        // 添加空值检查，增强健壮性
        W source = getSource();
        if (source == null) {
            throw new IllegalStateException("Wrapped mapping source cannot be null");
        }
        return source.asArray(uniqueness);
    }

    /**
     * 将映射转换为Map形式（委托给源映射）
     * <p>
     * 该实现直接调用源映射的{@link Mapping#asMap(boolean)}方法，
     * 包装器不做额外处理。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的映射实例
     * @see Mapping#asMap(boolean)
     */
    @Override
    default Mapping<K, V> asMap(boolean uniqueness) {
        // 添加空值检查，增强健壮性
        W source = getSource();
        if (source == null) {
            throw new IllegalStateException("Wrapped mapping source cannot be null");
        }
        return source.asMap(uniqueness);
    }
}