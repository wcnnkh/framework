package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.concurrent.limit.DisposableLimiter;

/**
 * 抽象键值对注册实现，提供基本的注册生命周期管理和限制器功能。
 * <p>
 * 该类继承自{@link LimitableRegistration}，实现了{@link EntryRegistration}接口，
 * 为键值对注册提供了基础实现，包括默认的可处置限制器和生命周期状态管理。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>生命周期管理：支持注册的启动、停止和取消状态</li>
 *   <li>资源限制：通过{@link DisposableLimiter}实现资源使用限制</li>
 *   <li>抽象方法：强制子类实现键值获取逻辑</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * 
 * @author soeasy.run
 * @see LimitableRegistration
 * @see EntryRegistration
 * @see DisposableLimiter
 */
public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {

    /**
     * 默认构造函数，初始化带有可处置限制器的注册
     * <p>
     * 使用{@link DisposableLimiter}作为默认限制器，允许注册在使用完毕后释放资源。
     */
    public AbstractEntryRegistration() {
        super(new DisposableLimiter());
    }
}