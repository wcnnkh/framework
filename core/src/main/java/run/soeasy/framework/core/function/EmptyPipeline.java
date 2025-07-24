package run.soeasy.framework.core.function;

/**
 * 空流水线实现，提供无实际资源的流水线操作。
 * 该实现是单例模式，用于表示没有有效资源的流水线状态，
 * 所有操作均为无操作或返回默认值，适用于需要占位或默认流水线的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}获取唯一实例</li>
 *   <li>无资源状态：{@link #get()}始终返回null</li>
 *   <li>已关闭状态：{@link #isClosed()}始终返回true</li>
 *   <li>无操作方法：{@link #close()}方法无实际操作</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为流水线操作的默认返回值</li>
 *   <li>表示资源获取失败的空状态</li>
 *   <li>占位用途，避免空指针检查</li>
 *   <li>简化流水线操作的初始状态处理</li>
 * </ul>
 *
 * @param <T> 流水线处理的资源类型（无实际意义）
 * @param <E> 可能抛出的异常类型（无实际意义）
 * @see Pipeline
 */
class EmptyPipeline<T, E extends Throwable> implements Pipeline<T, E> {
    /**
     * 空流水线的单例实例。
     * 所有对空流水线的引用应使用此实例，确保内存中只有一个空流水线实例。
     */
    static final Pipeline<?, ?> INSTANCE = new EmptyPipeline<>();

    /**
     * 获取流水线管理的资源，始终返回null。
     * 该实现不管理任何实际资源，此方法仅作为接口实现的占位。
     *
     * @return 始终返回null
     * @throws E 理论上可能抛出异常，但实际不会抛出
     */
    @Override
    public T get() throws E {
        return null;
    }

    /**
     * 检查流水线是否已关闭，始终返回true。
     * 空流水线始终处于已关闭状态，不管理任何需要释放的资源。
     *
     * @return 始终返回true
     */
    @Override
    public boolean isClosed() {
        return true;
    }

    /**
     * 释放流水线管理的资源，无实际操作。
     * 空流水线不管理任何资源，此方法仅作为接口实现的占位。
     *
     * @throws E 理论上可能抛出异常，但实际不会抛出
     */
    @Override
    public void close() throws E {
        // 空实现，无操作
    }

    /**
     * 空流水线的字符串表示。
     *
     * @return 固定字符串"EmptyPipeline"
     */
    @Override
    public String toString() {
        return "EmptyPipeline";
    }
}