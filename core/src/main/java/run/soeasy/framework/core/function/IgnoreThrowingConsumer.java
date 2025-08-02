package run.soeasy.framework.core.function;

/**
 * 忽略异常的消费者实现，不执行任何消费操作且不抛出异常。
 * 该实现提供了单例模式的忽略消费者实例，适用于需要占位或"无操作"消费场景，
 * 是函数式编程中处理空操作的基础组件之一。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}提供唯一实例，避免重复创建</li>
 *   <li>无操作实现：{@link #accept}方法为空实现，忽略所有输入参数</li>
 *   <li>无异常抛出：尽管实现了{@link ThrowingConsumer}接口，但实际不会抛出异常</li>
 *   <li>类型安全：严格实现泛型接口，适用于各种输入类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为消费链中的默认空操作步骤</li>
 *   <li>单元测试中模拟"无操作"的消费行为</li>
 *   <li>简化消费逻辑，在不需要实际消费时作为占位符</li>
 *   <li>配合函数组合操作，实现选择性执行消费逻辑</li>
 * </ul>
 *
 * @param <S> 消费操作的输入类型（实际未使用，仅满足接口泛型要求）
 * @param <E> 声明可能抛出的异常类型（实际不会抛出）
 * @see ThrowingConsumer
 */
class IgnoreThrowingConsumer<S, E extends Throwable> implements ThrowingConsumer<S, E> {
    /**
     * 忽略消费的单例实例。
     * 所有对忽略消费者的引用应使用此实例，确保内存中只有一个忽略消费者实例。
     */
    static final IgnoreThrowingConsumer<?, ?> INSTANCE = new IgnoreThrowingConsumer<>();

    /**
     * 空实现的消费方法，不执行任何操作且不抛出异常。
     * 该实现忽略输入参数{@code source}，仅作为接口实现的占位。
     *
     * @param source 输入参数（实际未使用）
     * @throws E 声明可能抛出异常，但实际不会抛出
     */
    @Override
    public void accept(S source) throws E {
        // 空实现，无操作
    }
}