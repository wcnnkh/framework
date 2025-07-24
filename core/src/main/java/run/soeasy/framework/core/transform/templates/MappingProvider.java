package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ServiceMap;

/**
 * 映射提供者，管理多个映射工厂并根据目标类型动态选择合适的工厂创建映射实例。
 * <p>
 * 该类继承自{@link ServiceMap}，维护一个从目标类型到映射工厂的映射关系，
 * 支持根据给定的源对象和目标类型描述符动态查找并调用匹配的映射工厂，
 * 生成对应的映射实例。适用于需要根据不同目标类型采用不同映射策略的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型驱动的工厂管理：基于目标类型注册和查找映射工厂</li>
 *   <li>动态映射创建：根据运行时类型信息选择最合适的映射工厂</li>
 *   <li>类型兼容匹配：支持查找与目标类型兼容的工厂（通过{@code assignableFrom}）</li>
 *   <li>泛型类型安全：通过泛型约束确保映射工厂和映射实例的类型一致性</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>类型擦除风险：泛型参数在运行时擦除，可能导致类型安全问题</li>
 *   <li>强制类型转换：{@code registerFactory}方法使用了未检查的类型转换</li>
 *   <li>工厂选择策略：当前实现仅返回第一个匹配的工厂，可能忽略更优选项</li>
 *   <li>线程安全：未明确保证线程安全，多线程环境下注册/查找可能存在竞争</li>
 *   <li>异常处理：未对工厂返回null的情况进行处理</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，必须实现{@link TypedValueAccessor}
 * @param <T> 映射类型，必须实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see MappingFactory
 * @see ServiceMap
 * @see TypeDescriptor
 */
@Getter
@Setter
public class MappingProvider<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
        extends ServiceMap<MappingFactory<Object, K, V, T>> implements MappingFactory<Object, K, V, T> {

    /**
     * 根据源对象和目标类型描述符获取映射实例
     * <p>
     * 查找与目标类型兼容的第一个映射工厂，并使用该工厂创建映射实例。
     * 若未找到匹配的工厂，返回null。
     * </p>
     * 
     * @param source 源对象，不可为null
     * @param requiredType 目标类型描述符，不可为null
     * @return 匹配的映射实例，若未找到工厂则返回null
     * @throws NullPointerException 若参数为null
     */
    @Override
    public T getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
        MappingFactory<? super Object, ? extends K, ? extends V, ? extends T> factory = assignableFrom(requiredType.getType())
                .first();
        if (factory == null) {
            return null;
        }
        return factory.getMapping(source, requiredType);
    }

    /**
     * 判断是否存在支持创建指定目标类型映射的工厂
     * <p>
     * 检查是否存在与目标类型兼容的映射工厂。
     * </p>
     * 
     * @param requiredType 目标类型描述符，不可为null
     * @return 存在匹配的工厂返回true，否则false
     * @throws NullPointerException 若参数为null
     */
    @Override
    public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
        return !assignableFrom(requiredType.getType()).isEmpty();
    }

    /**
     * 注册特定源类型的映射工厂
     * <p>
     * 将映射工厂注册到服务映射中，关联到指定的源类型。
     * 注意：该方法使用了未检查的类型转换，调用者需确保类型安全。
     * </p>
     * 
     * @param <S> 源类型
     * @param requriedType 源类型的Class对象，不可为null
     * @param mappingFactory 映射工厂，不可为null
     * @throws NullPointerException 若参数为null
     */
    @SuppressWarnings("unchecked")
    public <S> void registerFactory(Class<S> requriedType, MappingFactory<S, K, V, T> mappingFactory) {
        register(requriedType, (MappingFactory<Object, K, V, T>) mappingFactory);
    }
}