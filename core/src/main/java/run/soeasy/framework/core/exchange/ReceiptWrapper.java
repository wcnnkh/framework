package run.soeasy.framework.core.exchange;

/**
 * 回执包装器接口，定义对基础回执的装饰器模式实现。
 * 该接口继承自{@link Receipt}和{@link RegistrationWrapper}，
 * 允许在不修改原有回执的情况下增强其功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有状态查询操作委派给源回执</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>类型安全：通过泛型参数确保包装对象类型一致性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加回执的日志记录功能</li>
 *   <li>实现回执的条件性状态判断</li>
 *   <li>添加回执的结果转换或增强</li>
 *   <li>实现回执的异步处理</li>
 * </ul>
 *
 * @param <W> 被包装的基础回执类型
 * 
 * @author soeasy.run
 * @see Receipt
 * @see RegistrationWrapper
 */
public interface ReceiptWrapper<W extends Receipt> extends Receipt, RegistrationWrapper<W> {

    /**
     * 获取操作失败的原因
     * 默认实现将操作委派给源回执
     * 
     * @return 操作失败的原因，如果操作成功则返回null
     */
    @Override
    default Throwable cause() {
        return getSource().cause();
    }

    /**
     * 判断操作是否已完成
     * 默认实现将操作委派给源回执
     * 
     * @return 如果操作已完成返回true，否则返回false
     */
    @Override
    default boolean isDone() {
        return getSource().isDone();
    }

    /**
     * 判断操作是否成功
     * 默认实现将操作委派给源回执
     * 
     * @return 如果操作成功返回true，否则返回false
     */
    @Override
    default boolean isSuccess() {
        return getSource().isSuccess();
    }
}