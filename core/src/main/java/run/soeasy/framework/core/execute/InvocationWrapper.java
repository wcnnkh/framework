package run.soeasy.framework.core.execute;

/**
 * 方法调用包装器函数式接口，继承自{@link Invocation}和{@link ExecutionWrapper}，
 * 采用装饰器模式对方法调用上下文进行包装，支持在不修改原始逻辑的前提下为方法调用添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强方法调用行为的场景，如参数验证、返回值处理、异常拦截等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始方法调用并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源调用上下文，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的方法调用上下文类型，需实现{@link Invocation}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数校验：在方法调用前验证参数合法性</li>
 *   <li>返回值处理：对方法返回值进行转换或增强</li>
 *   <li>异常拦截：捕获并处理方法抛出的异常</li>
 *   <li>性能监控：记录方法执行时间和性能指标</li>
 *   <li>事务管理：在方法调用前后添加事务控制逻辑</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Invocation
 * @see ExecutionWrapper
 */
@FunctionalInterface
public interface InvocationWrapper<W extends Invocation> extends Invocation, ExecutionWrapper<W> {

    /**
     * 获取被包装方法调用的目标对象
     * <p>
     * 该默认实现将调用转发给被包装的源调用上下文的{@link Invocation#getTarget()}方法，
     * 子类可覆盖此方法返回自定义的目标对象（如代理对象）。
     * 
     * @return 方法调用的目标对象
     */
    @Override
    default Object getTarget() {
        return getSource().getTarget();
    }

    /**
     * 设置被包装方法调用的目标对象
     * <p>
     * 该默认实现将调用转发给被包装的源调用上下文的{@link Invocation#setTarget(Object)}方法，
     * 子类可覆盖此方法添加额外逻辑（如验证目标对象类型）。
     * 
     * @param target 目标对象
     */
    @Override
    default void setTarget(Object target) {
        getSource().setTarget(target);
    }
}