package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 服务注入器容器，管理多个服务注入器并批量执行注入操作。
 * <p>
 * 该容器继承自{@link ServiceContainer}，专门用于存储和管理{@link ServiceInjector}实例，
 * 实现了{@link ServiceInjector}接口以支持将服务实例注入到所有注册的注入器中，
 * 形成责任链模式的服务注入机制。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>批量注入：调用{@link #inject}时，所有注册的{@link ServiceInjector}都会执行注入</li>
 *   <li>有序执行：继承自{@link ServiceContainer}，支持按顺序执行注入器</li>
 *   <li>动态管理：支持动态注册、注销注入器，实时影响注入行为</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see ServiceContainer
 * @see ServiceInjector
 */
public class ServiceInjectors<S> extends ServiceContainer<ServiceInjector<? super S>> implements ServiceInjector<S> {

    /**
     * 执行服务实例的注入操作
     * <p>
     * 将服务实例传递给所有注册的{@link ServiceInjector}，按容器中的顺序依次执行注入。
     * 
     * @param service 要注入的服务实例
     * @return 注册句柄，可用于取消注入操作
     */
    @Override
    public Registration inject(S service) {
        return Registration.registers(this, (e) -> e.inject(service));
    }
}