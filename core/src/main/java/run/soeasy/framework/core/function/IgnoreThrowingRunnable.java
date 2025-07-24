package run.soeasy.framework.core.function;

/**
 * 忽略异常的可运行任务实现，不执行任何操作且不抛出异常。
 * 该实现提供了单例模式的忽略任务实例，适用于需要占位或"无操作"任务的场景，
 * 是函数式编程中处理空操作的基础组件之一。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}提供唯一实例，避免重复创建</li>
 *   <li>无操作实现：{@link #run}方法为空实现，不执行任何任务逻辑</li>
 *   <li>无异常抛出：尽管实现了{@link ThrowingRunnable}接口，但实际不会抛出异常</li>
 *   <li>类型安全：严格实现泛型接口，适用于各种异常类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为任务链中的默认空操作步骤</li>
 *   <li>单元测试中模拟"无操作"的任务行为</li>
 *   <li>简化任务组合逻辑，在不需要实际执行时作为占位符</li>
 *   <li>配合函数式操作，实现选择性执行任务逻辑</li>
 * </ul>
 *
 * @param <E> 声明可能抛出的异常类型（实际不会抛出）
 * @see ThrowingRunnable
 */
class IgnoreThrowingRunnable<E extends Throwable> implements ThrowingRunnable<E> {
    /**
     * 忽略任务的单例实例。
     * 所有对忽略任务的引用应使用此实例，确保内存中只有一个忽略任务实例。
     */
    static final IgnoreThrowingRunnable<?> INSTANCE = new IgnoreThrowingRunnable<>();

    /**
     * 空实现的任务执行方法，不执行任何操作且不抛出异常。
     * 该实现仅作为接口实现的占位，不包含任何实际任务逻辑。
     *
     * @throws E 声明可能抛出异常，但实际不会抛出
     */
    @Override
    public void run() throws E {
        // 空实现，无操作
    }
}