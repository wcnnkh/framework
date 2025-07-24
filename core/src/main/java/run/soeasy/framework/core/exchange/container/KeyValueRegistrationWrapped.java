package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 键值对注册包装器，用于增强和扩展{@link KeyValueRegistration}的功能。
 * <p>
 * 该类继承自{@link PayloadRegistrationWrapped}，实现了{@link KeyValueRegistrationWrapper}接口，
 * 允许将一个基础键值对注册对象包装为具有额外功能的注册对象，如组合注册、条件取消等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>键值对管理：继承源注册对象的键值对数据</li>
 *   <li>注册包装：通过构造函数传入被包装的源注册对象</li>
 *   <li>组合注册：支持与其他注册对象组合形成新的注册</li>
 *   <li>相关注册管理：维护与当前注册相关的其他注册对象集合</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的注册类型，需继承{@link KeyValueRegistration}&lt;{@link K}, {@link V}&gt;
 * 
 * @author soeasy.run
 * @see KeyValueRegistration
 * @see KeyValueRegistrationWrapper
 * @see PayloadRegistrationWrapped
 */
public class KeyValueRegistrationWrapped<K, V, W extends KeyValueRegistration<K, V>>
        extends PayloadRegistrationWrapped<KeyValue<K, V>, W> implements KeyValueRegistrationWrapper<K, V, W> {

    /**
     * 构造函数，初始化键值对注册包装器
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param relatedRegistrations 相关注册对象集合，不可为null
     * @throws NullPointerException 若source或relatedRegistrations为null
     */
    public KeyValueRegistrationWrapped(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
        super(source, relatedRegistrations);
    }

    /**
     * 复制构造函数，用于创建具有相同配置的新包装器
     * 
     * @param context 要复制的注册包装器上下文
     */
    protected KeyValueRegistrationWrapped(RegistrationWrapped<W> context) {
        super(context);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将当前注册与指定注册组合，返回新的键值对注册包装器实例。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的键值对注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public KeyValueRegistrationWrapped<K, V, W> and(Registration registration) {
        return new KeyValueRegistrationWrapped<>(super.combine(registration));
    }

    /**
     * 获取注册的键值对数据
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link KeyValueRegistration#getPayload()}方法。
     * 
     * @return 注册的键值对数据
     * @see KeyValueRegistration#getPayload()
     */
    @Override
    public KeyValue<K, V> getPayload() {
        return super.getPayload();
    }
}