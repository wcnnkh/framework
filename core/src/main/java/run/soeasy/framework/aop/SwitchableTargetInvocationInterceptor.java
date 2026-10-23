package run.soeasy.framework.aop;

import lombok.Data;
import run.soeasy.framework.core.execute.Invocation;

/**
 * 可切换目标对象的调用拦截器，实现{@link InvocationInterceptor}接口，
 * 用于在AOP拦截链条中动态设置方法调用的目标对象，支持动态切换被代理的目标实例，
 * 为需要灵活变更目标对象的场景（如动态代理切换、目标对象热替换等）提供基础支持。
 * 
 * <p>该拦截器的核心功能是将自身持有的目标对象设置到{@link Invocation}上下文当中，
 * 确保后续的拦截器或目标方法调用使用当前设置的目标对象，从而实现目标对象的动态切换，
 * 且不影响拦截链的其他处理逻辑。
 * 
 * @author soeasy.run
 * @see InvocationInterceptor
 * @see Invocation
 */
@Data
public class SwitchableTargetInvocationInterceptor implements InvocationInterceptor {

    /**
     * 当前拦截器所关联的目标对象（被代理的实际对象，可动态变更）
     */
    private final Object target;

    /**
     * 拦截方法调用，设置目标对象并继续执行
     * 
     * <p>处理逻辑：
     * 1. 将当前拦截器持有的{@code target}设置到{@link Invocation}上下文（通过{@link Invocation#setTarget(Object)}）；
     * 2. 调用{@link Invocation#execute()}继续执行后续拦截器或目标方法，确保使用更新后的目标对象。
     * 
     * @param executor 方法调用上下文（包含当前调用的目标对象、方法、参数等信息）
     * @return 方法调用结果（使用切换后的目标对象执行方法的返回值）
     * @throws Throwable 方法执行过程中可能抛出的异常（由目标方法或后续拦截器产生）
     */
    @Override
    public Object intercept(Invocation executor) throws Throwable {
        // 设置当前拦截器持有的目标对象到调用上下文
        executor.setTarget(target);
        // 继续执行后续流程（下一个拦截器或目标方法）
        return executor.execute();
    }
}