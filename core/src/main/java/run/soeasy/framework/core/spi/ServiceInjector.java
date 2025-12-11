package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.exchange.Operation;

/**
 * 服务注入功能接口，定义服务实例的注入操作契约。
 * <p>
 * 该函数式接口用于将服务实例注入到目标组件中，返回的注册句柄可用于管理注入的生命周期，
 * 适用于依赖注入框架或服务管理场景，支持动态控制服务的注入与注销。
 *
 * @param <S> 服务实例的类型，即被注入的服务对象类型
 * 
 * @author soeasy.run
 * @see Operation
 * @see ServiceInjectors
 */
@FunctionalInterface
public interface ServiceInjector<S> {
    /**
     * 执行服务实例的注入操作
     * <p>
     * 将指定的服务实例注入到目标组件中，并返回注册句柄用于管理该注入操作。
     * 实现类应确保注入操作的幂等性和线程安全性。
     * 
     * @param service 要注入的服务实例，不可为null
     * @return 注册句柄，可用于取消注入或监听注入状态
     * @throws NullPointerException 若service为null
     */
    Operation inject(S service);
}