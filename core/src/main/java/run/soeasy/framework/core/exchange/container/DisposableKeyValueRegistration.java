package run.soeasy.framework.core.exchange.container;

import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 带键值对的一次性注册实现，在取消时执行基于键值的处理逻辑。
 * <p>
 * 该类继承自{@link AbstractLifecycleRegistration}，
 * 实现了{@link KeyValueRegistration}接口，
 * 允许在注册被取消时执行特定的清理或处理操作，
 * 并提供键值对的访问能力。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>一次性执行：确保取消时的处理逻辑仅执行一次</li>
 *   <li>生命周期管理：继承父类的生命周期管理能力</li>
 *   <li>键值对访问：提供对键和值的不可变访问</li>
 *   <li>自定义处理：通过BiPredicate传入基于键值的处理逻辑</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * 
 * @author soeasy.run
 * @see AbstractLifecycleRegistration
 * @see KeyValueRegistration
 */
@RequiredArgsConstructor
public class DisposableKeyValueRegistration<K, V> extends AbstractLifecycleRegistration
        implements KeyValueRegistration<K, V> {
    
    /** 注册的键 */
    private final K key;
    
    /** 注册的值 */
    private final V value;
    
    /** 注册取消时执行的基于键值的处理逻辑 */
    @NonNull
    private final BiPredicate<? super K, ? super V> runnable;

    /**
     * 获取注册的键
     * 
     * @return 注册的键
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * 获取注册的值
     * 
     * @return 注册的值
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * 尝试取消注册，并执行预先指定的基于键值的处理逻辑
     * <p>
     * 在父类取消逻辑的基础上，增加自定义处理逻辑的执行。
     * 只有当父类取消条件和传入的条件都满足时，才会执行处理逻辑。
     * 
     * @param cancel 自定义取消条件提供者
     * @return 取消操作结果，true表示成功，false表示失败
     */
    @Override
    public boolean cancel(BooleanSupplier cancel) {
        return super.cancel(() -> {
            if (cancel.getAsBoolean()) {
                return runnable.test(key, value);
            }
            return false;
        });
    }
}