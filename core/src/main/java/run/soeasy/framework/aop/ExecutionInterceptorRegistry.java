package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;

/**
 * 执行拦截器注册中心，继承自{@link ConfigurableServices}并实现{@link ExecutionInterceptor}接口，
 * 用于管理和注册多个{@link ExecutionInterceptor}实例，同时自身作为一个拦截器，
 * 将所有注册的拦截器组成拦截链并按序执行，是AOP框架中拦截器的集中管理与执行入口。
 * 
 * <p>该类通过SPI（服务提供者接口）机制管理拦截器，支持动态注册和配置拦截器，
 * 在拦截执行时，将所有注册的拦截器构建为{@link ExecutionInterceptorChain}，
 * 实现拦截器的链式调用，并最终执行{@code nextChain}（目标执行逻辑）。
 * 
 * @author soeasy.run
 * @see ConfigurableServices
 * @see ExecutionInterceptor
 * @see ExecutionInterceptorChain
 */
public class ExecutionInterceptorRegistry extends ConfigurableServices<ExecutionInterceptor>
		implements ExecutionInterceptor {

    /**
     * 拦截链执行完毕后触发的下一个执行节点（通常为目标业务逻辑）
     */
	private Execution nextChain;

    /**
     * 构造执行拦截器注册中心，指定服务类为{@link ExecutionInterceptor}
     */
	public ExecutionInterceptorRegistry() {
		super(ServiceComparator.defaultServiceComparator());
	}

    /**
     * 拦截执行过程，将所有注册的拦截器组成链并执行
     * 
     * <p>处理逻辑：
     * 1. 基于当前注册的所有拦截器（通过{@link #iterator()}获取）和{@code nextChain}，创建拦截器链；
     * 2. 调用拦截链的{@link ExecutionInterceptorChain#intercept(Execution)}方法，
     *    按注册顺序执行所有拦截器，最终触发目标执行逻辑。
     * 
     * @param function 执行上下文（非空，包含执行所需的信息）
     * @return 执行结果（经过所有拦截器处理后的目标逻辑返回值）
     * @throws Throwable 拦截过程中任意拦截器或目标逻辑抛出的异常
     */
	@Override
	public Object intercept(@NonNull Execution function) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(iterator(), nextChain);
		return chain.intercept(function);
	}

    /**
     * 设置拦截链执行后的下一个执行节点
     * 
     * @param nextChain 下一个执行节点（目标执行逻辑）
     */
    public void setNextChain(Execution nextChain) {
        this.nextChain = nextChain;
    }

    /**
     * 获取拦截链执行后的下一个执行节点
     * 
     * @return 下一个执行节点
     */
    public Execution getNextChain() {
        return nextChain;
    }
}