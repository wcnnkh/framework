package run.soeasy.framework.core.function;

import lombok.RequiredArgsConstructor;

/**
 * 恒值布尔谓词实现，始终返回固定的布尔值。
 * 该实现提供了两个预定义实例：{@link #TRUE}和{@link #FALSE}，
 * 分别表示始终返回true和始终返回false的谓词，适用于需要固定条件判断的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：预定义TRUE和FALSE静态实例，避免重复创建</li>
 *   <li>无异常抛出：{@link #test}方法始终返回固定值，不会抛出异常</li>
 *   <li>类型安全：实现{@link ThrowingPredicate}接口，支持泛型类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为谓词链中的默认条件（如始终通过/拒绝）</li>
 *   <li>简化单元测试中的条件模拟</li>
 *   <li>占位用途，避免空指针检查</li>
 *   <li>需要固定布尔返回值的函数式编程场景</li>
 * </ul>
 *
 * @param <S> 谓词判断的输入类型（实际未使用，仅满足接口泛型要求）
 * @param <E> 声明可能抛出的异常类型（实际不会抛出）
 * @see ThrowingPredicate
 */
@RequiredArgsConstructor
class AlwaysBooleanPredicat<S, E extends Throwable> implements ThrowingPredicate<S, E> {
    /**
     * 始终返回true的谓词实例。
     * 该实例的{@link #test}方法始终返回true，适用于需要默认通过条件的场景。
     */
    static final AlwaysBooleanPredicat<?, ?> TRUE = new AlwaysBooleanPredicat<>(true);
    
    /**
     * 始终返回false的谓词实例。
     * 该实例的{@link #test}方法始终返回false，适用于需要默认拒绝条件的场景。
     */
    static final AlwaysBooleanPredicat<?, ?> FALSE = new AlwaysBooleanPredicat<>(false);

    /**
     * 存储固定返回的布尔值。
     */
    private final boolean value;

    /**
     * 始终返回构造时指定的布尔值，不会抛出异常。
     * 该实现忽略输入参数{@code source}，仅返回预定义的{@code value}。
     *
     * @param source 输入参数（实际未使用）
     * @return 构造时指定的布尔值
     * @throws E 声明抛出异常，但实际不会抛出
     */
    @Override
    public boolean test(S source) throws E {
        return value;
    }
}