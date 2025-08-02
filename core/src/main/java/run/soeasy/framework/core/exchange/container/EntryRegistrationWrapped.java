package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 条目注册包装器，用于增强和扩展{@link EntryRegistration}的功能。
 * <p>
 * 该类继承自{@link KeyValueRegistrationWrapped}，实现了{@link EntryRegistrationWrapper}接口，
 * 允许将一个基础条目注册对象包装为具有额外功能的注册对象，如组合注册、条件取消等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>条目管理：继承源注册对象的键值对数据</li>
 *   <li>注册包装：通过构造函数传入被包装的源注册对象</li>
 *   <li>组合注册：支持与其他注册对象组合形成新的注册</li>
 *   <li>相关注册管理：维护与当前注册相关的其他注册对象集合</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的注册类型，需继承{@link EntryRegistration}
 * 
 * @author soeasy.run
 * @see EntryRegistration
 * @see EntryRegistrationWrapper
 * @see KeyValueRegistrationWrapped
 */
public class EntryRegistrationWrapped<K, V, W extends EntryRegistration<K, V>>
        extends KeyValueRegistrationWrapped<K, V, W> implements EntryRegistrationWrapper<K, V, W> {

    /**
     * 构造函数，初始化条目注册包装器
     * <p>
     * 包装指定的条目注册对象，并关联相关注册集合。
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param relatedRegistrations 相关注册对象集合，不可为null
     * @throws NullPointerException 若source或relatedRegistrations为null
     */
    public EntryRegistrationWrapped(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
        super(source, relatedRegistrations);
    }

    /**
     * 复制构造函数，用于创建具有相同配置的新包装器
     * <p>
     * 从现有包装器复制上下文信息，创建新的条目注册包装器。
     * 
     * @param context 要复制的键值对注册包装器上下文
     */
    protected EntryRegistrationWrapped(KeyValueRegistrationWrapped<K, V, W> context) {
        super(context);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 将当前注册与指定注册组合，返回新的条目注册包装器实例。
     * 新包装器将包含当前注册和指定注册的组合逻辑。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的条目注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public EntryRegistrationWrapped<K, V, W> and(@NonNull Registration registration) {
        return new EntryRegistrationWrapped<>(super.and(registration));
    }
}