package run.soeasy.framework.core.execute;

/**
 * 执行上下文接口，定义了可执行元素的执行环境和上下文信息，
 * 封装了可执行元素的元数据、执行参数，并提供执行方法。
 * <p>
 * 该接口是框架中方法执行的核心抽象，将可执行元素与其执行环境解耦，
 * 支持在不同上下文中复用同一可执行元素，同时允许动态修改执行参数。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>元数据访问：通过{@link #getMetadata()}获取可执行元素的完整元数据</li>
 *   <li>参数操作：通过{@link #getArguments()}获取和修改执行参数</li>
 *   <li>上下文执行：通过{@link #execute()}方法执行可执行元素并返回结果</li>
 *   <li>异常处理：直接抛出执行过程中产生的原始异常</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>拦截器机制：在方法执行前后添加额外处理逻辑</li>
 *   <li>参数验证：执行前验证参数合法性</li>
 *   <li>重试机制：实现失败重试逻辑</li>
 *   <li>异步执行：将执行上下文提交到线程池实现异步调用</li>
 *   <li>执行监控：收集方法执行的性能指标</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see ExecutableMetadata
 */
public interface Execution {
    
    /**
     * 获取执行上下文关联的可执行元素元数据
     * <p>
     * 该元数据包含可执行元素的完整描述信息，如参数类型、返回类型、异常类型等。
     * </p>
     * 
     * @return 可执行元素元数据
     */
    ExecutableMetadata getMetadata();

    /**
     * 获取执行参数数组
     * <p>
     * 返回的数组是可修改的，修改数组元素将直接影响后续的执行过程。
     * 数组的长度和元素类型应与{@link #getMetadata()}中定义的参数模板匹配。
     * </p>
     * 
     * @return 执行参数数组，可修改
     */
    Object[] getArguments();

    /**
     * 执行可执行元素并返回结果
     * <p>
     * 该方法使用当前上下文中的参数（通过{@link #getArguments()}获取）执行可执行元素，
     * 并返回执行结果。执行过程中可能抛出的异常将直接抛出。
     * </p>
     * 
     * @return 执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    Object execute() throws Throwable;
}