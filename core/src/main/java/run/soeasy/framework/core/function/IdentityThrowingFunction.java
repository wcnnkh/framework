package run.soeasy.framework.core.function;

/**
 * 恒等函数实现，始终返回输入参数本身，不进行任何转换。
 * 该实现提供了单例模式的恒等函数实例，适用于需要"无操作"转换的场景，
 * 是函数式编程中常用的基础组件之一。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}提供唯一实例，避免重复创建</li>
 *   <li>无转换逻辑：{@link #apply}方法直接返回输入参数，时间复杂度O(1)</li>
 *   <li>无异常抛出：尽管实现了{@link ThrowingFunction}接口，但实际不会抛出异常</li>
 *   <li>类型安全：严格保持输入输出类型一致，符合恒等函数定义</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为函数链中的默认转换步骤（如不需要实际转换时）</li>
 *   <li>单元测试中模拟"无操作"的函数行为</li>
 *   <li>简化函数组合逻辑，作为组合起点或终点</li>
 *   <li>需要保持输入输出类型一致的场景（如类型擦除后的强制转换）</li>
 * </ul>
 *
 * @param <T> 函数的输入/输出类型，必须保持一致
 * @param <E> 声明可能抛出的异常类型（实际不会抛出）
 * @see ThrowingFunction
 */
class IdentityThrowingFunction<T, E extends Throwable> implements ThrowingFunction<T, T, E> {
    /**
     * 恒等函数的单例实例。
     * 所有对恒等函数的引用应使用此实例，确保内存中只有一个恒等函数实例。
     */
    static final IdentityThrowingFunction<?, ?> INSTANCE = new IdentityThrowingFunction<>();

    /**
     * 应用恒等函数，直接返回输入参数。
     * 该实现不进行任何转换操作，仅作为类型安全的参数传递。
     *
     * @param source 输入参数
     * @return 与输入参数完全相同的输出
     * @throws E 声明可能抛出异常，但实际不会抛出
     */
    @Override
    public T apply(T source) throws E {
        return source;
    }
}